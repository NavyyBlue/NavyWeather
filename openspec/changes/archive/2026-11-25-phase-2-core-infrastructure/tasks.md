# Tasks

## 1. Networking (`core:network`)

- [x] 1.1 Create `core:network` (library convention + serialization) with `HttpClientFactory` (tolerant JSON, `expectSuccess`, connect 10 s / request 15 s timeouts, logging only when requested), `platformHttpEngine()` actuals (OkHttp on Android and JVM, Darwin on iOS) and `networkModule(enableHttpLogging)`; verify it compiles for every target
- [x] 1.2 Add `safeApiCall` mapping response, I/O, serialization and other exceptions to `AppError` while rethrowing cancellation; verify `SafeApiCallTest` with `MockEngine` covers success, 503 → `Http(503)`, malformed body → `Serialization`, missing required field → `Serialization`, I/O failure → `Network`, unknown fields ignored, and cancellation propagating without a result (specs/error-handling, specs/networking)
- [x] 1.3 Verify the timeout and logging requirements: a test with a `MockEngine` that delays longer than the request timeout gets `Network`, and a test checks that a client created without logging has no `Logging` plugin installed while one created with logging does (specs/networking)

## 2. Local cache (`core:database`)

- [x] 2.1 Create `core:database` (library + room conventions) with the three entities, `FavoriteCityWithWeather`, `AppDatabase` v1 with schema export, `buildAppDatabase()` (bundled driver, IO dispatcher) and platform builder modules (Android private storage, iOS Application Support, JVM temp dir); verify KSP generates code for every target and `schemas/.../1.json` is produced
- [x] 2.2 Add `FavoriteCityDao` (observe all, observe with weather via LEFT JOIN, observe is-favorite, upsert, delete by id); verify `FavoriteCityDaoTest` on an in-memory database covers upsert without duplicates, removal, is-favorite transitions and a favorite gaining its temperature when weather is stored (specs/local-cache)
- [x] 2.3 Add `ForecastDao` with transactional `replaceForecast`; verify `ForecastDaoTest` covers storing current conditions, days ordered by date, a moved forecast window dropping old days, and a repeated date not failing the replacement (specs/local-cache)
- [x] 2.4 Add `databaseModule`; verify a test resolves the DAOs from a Koin graph built with an in-memory builder

## 3. User preferences (`core:datastore`)

- [x] 3.1 Create `core:datastore` with `TemperatureUnit.fromCelsius`, the `UserPreferencesRepository` interface, its DataStore implementation (Celsius default and fallback, `distinctUntilChanged`) and platform file-path modules; verify `TemperatureUnitTest` (21.6 °C → 22, 20 °C → 68 °F) and `DataStoreUserPreferencesRepositoryTest` (default, persisted across a new instance on the same file, unchanged value not re-emitted, unknown stored value read as Celsius) on a temporary file per test target (specs/user-preferences)

## 4. Weather data (`core:weather`)

- [x] 4.1 Create `core:weather` with `OpenMeteoForecastApi` and its DTOs; verify `OpenMeteoForecastApiTest` with `MockEngine` asserts the request URL and parameters (coordinates, current and daily fields, `forecast_days=7`, `timezone=auto`) and parses a sample response (specs/weather-data)
- [x] 4.2 Add `ForecastSync` (sync into the cache with the injected clock, last sync time, zipped daily arrays) and `weatherModule`; verify `ForecastSyncTest` with `MockEngine` and an in-memory database covers a successful sync with its download time, last sync time before and after, a 503 leaving the cached forecast untouched and returning `Http(503)`, and uneven daily arrays storing the common days (specs/weather-data)

## 5. Design system (`core:designsystem`)

- [x] 5.1 Create `core:designsystem` (compose convention + MaterialKolor, Compose UI test for tests) with `NavySeed`, `NavyWeatherTheme` (Fidelity brand scheme, dynamic color opt-in on Android 12+, expressive motion), `NavyShapes`, `Spacing`, `NavyTypography` and `tabularNumbers()`; verify `ThemeTest` asserts light `primaryContainer == #1F3A93`, dark `primary` differs from light `primary`, and requesting dynamic color off Android keeps the brand scheme (specs/design-system)
- [x] 5.2 Add `LoadingState`, `EmptyState`, `ErrorState` and the shared scrolling `StateMessage`; verify `StatesTest` covers retry invoked once on tap, the retry button reachable by scrolling in a short window at 200% font scale, and the loading state's "Loading" description (specs/design-system)
- [x] 5.3 Add English and Spanish strings and `AppError.message()` / `loadMessage()`; verify `ErrorMessageTest` (JVM and iOS) checks that every error kind has a non-empty message with `Http` codes sharing one, and `ErrorMessageLocaleTest` (JVM, where the test can switch the locale) checks the network message in English and Spanish and a distinct message per kind in both languages (specs/error-handling)

## 6. Decisions and documentation

- [x] 6.1 Write ADRs 0008 (Ktor with native engines), 0009 (Room KMP with bundled SQLite), 0010 (DataStore for preferences), 0011 (Koin), 0012 (offline-first single source of truth) and 0013 (brand color scheme from one seed) and add them to `docs/adr/README.md`
- [x] 6.2 Update `docs/architecture.md` (remove the Phase 2 markers, add the core module graph and the offline-first data flow, linking ADR-0012) and `docs/tech-stack.md` (link ADRs 0008–0011 and 0013); verify every library mentioned is in the catalog
- [x] 6.3 Write `docs/phases/phase-2-core-infrastructure.md` (deliverables, decisions, tests, commands) and update the README roadmap; verify the commands run as written

## 7. Integration check

- [x] 7.1 Register the five modules in `settings.gradle.kts`, run `./gradlew allTests` and compile every module for Android, iOS device and iOS simulator; verify all tests pass on the JVM and the iOS simulator and `openspec validate --changes --strict` passes
