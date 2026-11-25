# Design

## Context

Phase 1 delivered the build, `core:common` (`AppResult`/`AppError`), `core:mvi`, `core:navigation` and `core:testing`. This change adds the five infrastructure modules the features need (see proposal.md). Constraints:

- Features never depend on each other (ADR-0002), so anything two features need has to live in core. Both favorites and forecast need to download a forecast.
- Requirements are in `specs/networking`, `specs/local-cache`, `specs/weather-data`, `specs/user-preferences`, `specs/design-system` and the `specs/error-handling` delta.
- There is no app yet, so every behavior has to be checked by tests on the JVM host and the iOS simulator (ADR-0005).

## Goals / Non-Goals

**Goals:**
- Each core module exposes a small public API plus a Koin module, and hides its library types where it reasonably can.
- The local database is the single source of truth: screens read from it, and the network only writes to it.
- Every requirement is covered by a test that runs on both test targets.

**Non-Goals:**
- Freshness rules, repositories, use cases and domain models. They belong to each feature (Phases 3–4).
- The `shared` composition root. It provides `Clock`, `LanguageProvider` and the debug flag for HTTP logging in Phase 3.
- Database migrations. The schema starts at version 1 and is exported so future migrations can be tested.

## Decisions

The decisions with lasting impact are recorded as ADRs written in this change:

| ADR | Decision |
|---|---|
| [0008](../../../../docs/adr/0008-ktor-client-with-native-engines.md) | Ktor Client with native engines and kotlinx.serialization for HTTP |
| [0009](../../../../docs/adr/0009-room-kmp-with-bundled-sqlite.md) | Room KMP with bundled SQLite for the local cache |
| [0010](../../../../docs/adr/0010-datastore-for-preferences.md) | DataStore Preferences for user settings |
| [0011](../../../../docs/adr/0011-koin-for-dependency-injection.md) | Koin for dependency injection, with a test that verifies the graph |
| [0012](../../../../docs/adr/0012-offline-first-single-source-of-truth.md) | Offline-first: the local database is the single source of truth, and forecast download is shared in `core:weather` |
| [0013](../../../../docs/adr/0013-brand-color-scheme-from-one-seed.md) | Color scheme generated from one brand seed with MaterialKolor (Fidelity) |

### Module graph

```
core:designsystem ──► core:common
core:weather ──► core:network ──► core:common
            └──► core:database
core:datastore        (standalone)
```

`core:weather` uses `core:network` and `core:database` as implementation dependencies, so features that use `ForecastSync` do not see Ktor. `core:network` exposes Ktor's `HttpClient` as `api`, because `core:weather` and the search feature (Phase 4) build their API clients on it.

### `core:network`
- `HttpClientFactory.create(engine, logLevel)` builds the client with `expectSuccess = true`, JSON (`ignoreUnknownKeys`, `explicitNulls = false`), `HttpTimeout` (connect 10 s, request 15 s, and a 10 s socket timeout, because the Darwin engine ignores the connect timeout and only applies the socket one) and, only when `logLevel != NONE`, the `Logging` plugin routed through Kermit. Taking the engine as a parameter lets tests use `MockEngine`.
- `platformHttpEngine()` is an `expect` function: OkHttp on Android and on the JVM test host, Darwin on iOS.
- `safeApiCall { }` rethrows `CancellationException` and maps the rest:

  | Exception | Error |
  |---|---|
  | `ResponseException` | `Http(code)` |
  | `IOException`, including timeouts | `Network` |
  | `SerializationException`, `ContentConvertException` | `Serialization` |
  | anything else | `Unknown(message)` |

- `networkModule(enableHttpLogging: Boolean)` exposes the client as a Koin `single`. Phase 3 passes the debug flag.

### `core:database`
- `AppDatabase` (version 1, `exportSchema = true`) has three entities:
  - `favorite_cities`: `id` is the Open-Meteo place id.
  - `current_weather`: keyed by `cityId`, with `fetchedAtEpochMillis`.
  - `daily_forecast`: composite key (`cityId`, `date`).
- DAOs:
  - `FavoriteCityDao`: `observeAll`, `observeAllWithWeather` (a LEFT JOIN with `current_weather` into `FavoriteCityWithWeather`), `observeIsFavorite`, `upsert` and `deleteById`.
  - `ForecastDao`: `observeCurrent`, `observeDaily`, and a `@Transaction replaceForecast` that upserts the current conditions, deletes the city's days and then upserts the new ones. Upsert, not insert, is what makes a repeated date harmless.
- `buildAppDatabase()` sets `BundledSQLiteDriver` and `Dispatchers.IO` on every platform. A platform Koin module provides the builder:
  - Android: `context.getDatabasePath`.
  - iOS: `NSApplicationSupportDirectory`, created if missing.
  - JVM: a temp-dir file, for completeness.
- Tests use an in-memory builder per test target (`expect fun inMemoryDatabase()`).
- Weather entities have no foreign key to `favorite_cities`, because the forecast screen caches places that are not favorites.

### `core:datastore`
- `UserPreferencesRepository` is an interface, so features depend on it rather than on DataStore. `DataStoreUserPreferencesRepository` stores the unit's name as a string, falls back to Celsius for unknown values, and applies `distinctUntilChanged()`. A corrupted file is replaced with empty preferences (`ReplaceFileCorruptionHandler`), and read errors fall back to the defaults, so settings never crash a screen.
- `TemperatureUnit.fromCelsius(Double): Int` converts and rounds.
- DataStore allows only one instance per file, so it is a Koin `single`. Each platform module provides the file path, and tests use a temporary file per test target.

### `core:weather`
- `OpenMeteoForecastApi(client)` sends the request described in `specs/weather-data` and decodes `ForecastResponseDto`. Open-Meteo returns daily data as parallel arrays, so the DTO keeps them that way.
- `ForecastSync(api, dao, clock)`:
  - `sync(cityId, lat, lon)` runs the download, the mapping and `replaceForecast` inside one `safeApiCall`, so a failed cache write is returned as an error too. The cache is written only after a complete, parsed response.
  - `lastSyncedAtEpochMillis(cityId)` reads the cached `fetchedAtEpochMillis`.
  - Daily mapping zips the arrays up to the shortest one.
- `kotlin.time.Clock` is injected so tests control the download time.

### `core:designsystem`
- `NavyWeatherTheme(darkTheme, dynamicColor = false)` uses `rememberDynamicColorScheme(seed = #1F3A93, style = Fidelity)` inside `MaterialExpressiveTheme` with `MotionScheme.expressive()`, `NavyTypography` and `NavyShapes`. `platformDynamicColorScheme` returns a scheme only on Android 12+, and `null` on iOS and the JVM.
- `Spacing` (4–48 dp), `NavyShapes`, the corner tokens and `TextStyle.tabularNumbers()` (`tnum`).
- `LoadingState` (an Expressive `LoadingIndicator` with a "Loading" content description), `EmptyState` and `ErrorState`. The last two share a private `StateMessage` that scrolls vertically and caps the text width at 360 dp. `ErrorState` uses a filled retry button.
- `AppError.message()` (composable) and `AppError.loadMessage()` (suspend, for snackbars) map each kind to a Compose Resources string in `values` (English) and `values-es` (Spanish).
- UI tests (`runComposeUiTest`, JVM and iOS) cover the theme colors, the retry action, scrolling and the messages. The expected strings are read from resources inside the composition, so tests do not depend on the host locale.

## Risks / Trade-offs

- [The Room KSP processor has to run for every target, and the build gets slower] → only `core:database` applies `navyweather.room`.
- [MaterialKolor output could change between versions, which would change the brand colors] → the version is pinned, and a test asserts that light `primaryContainer` equals the seed.
- [DataStore throws if two instances open the same file] → it is a single Koin `single`, and each test uses its own temporary file.
- [Compose UI tests on iOS are slower] → they are kept to the few behaviors the spec requires.
- [Open-Meteo might change its response] → the JSON is tolerant to new fields, and a missing required field becomes a data error rather than a crash.

## Open Questions

- None blocking. Wiring the HTTP logging debug flag and providing `Clock` both happen in the `shared` composition root (Phase 3).
