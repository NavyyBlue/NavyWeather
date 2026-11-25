# Phase 2 — Core infrastructure

**OpenSpec change:** `phase-2-core-infrastructure`
**Capabilities:** `networking`, `local-cache`, `weather-data`, `user-preferences`, `design-system` (new) and `error-handling` (extended)
**ADRs:** [0008](../adr/0008-ktor-client-with-native-engines.md), [0009](../adr/0009-room-kmp-with-bundled-sqlite.md), [0010](../adr/0010-datastore-for-preferences.md), [0011](../adr/0011-koin-for-dependency-injection.md), [0012](../adr/0012-offline-first-single-source-of-truth.md), [0013](../adr/0013-brand-color-scheme-from-one-seed.md)

## Goal

Build, once and in core modules, the infrastructure every feature needs: talking to Open-Meteo, storing data on the device, remembering settings and drawing screens. Features in Phases 3 and 4 only add their own logic on top.

## What this phase delivers

| Module | Delivered |
|---|---|
| `core:network` | One Ktor client (OkHttp / Darwin), tolerant JSON, 10 s / 15 s timeouts, opt-in request logging, `safeApiCall` mapping failures to `AppError` |
| `core:database` | Room with bundled SQLite: favorites, current weather and daily forecast; reactive DAOs, favorites joined with their temperature, atomic forecast replacement, exported schema; Android private storage and iOS Application Support |
| `core:datastore` | Temperature unit preference (Celsius default, persisted, emitted only on change) and rounding conversion |
| `core:weather` | Open-Meteo forecast client and `ForecastSync`, which writes a download into the cache and records its time |
| `core:designsystem` | Material 3 Expressive theme from the seed `#1F3A93` (Fidelity), shapes, spacing, tabular numbers, loading/empty/error states, error messages in English and Spanish |
| Build | Compose UI test setup moved into `navyweather.kmp.compose`; JVM tests allow native access and `sun.misc.Unsafe` so JDK 25 runs them without warnings |
| Docs | ADRs 0008–0013, architecture (core module graph and data flow), tech stack, this document |

## Key decisions

- **Ktor with native engines** ([ADR-0008](../adr/0008-ktor-client-with-native-engines.md)): one HTTP code path that still follows each platform's network settings.
- **Room KMP with bundled SQLite** ([ADR-0009](../adr/0009-room-kmp-with-bundled-sqlite.md)): reactive, compile-time checked queries and the same SQLite on both platforms.
- **DataStore for preferences** ([ADR-0010](../adr/0010-datastore-for-preferences.md)): asynchronous and Flow-based, unlike SharedPreferences, which is also Android only.
- **Koin** ([ADR-0011](../adr/0011-koin-for-dependency-injection.md)): no code generation; graph errors are caught by tests.
- **Offline-first** ([ADR-0012](../adr/0012-offline-first-single-source-of-truth.md)): screens read the cache; `ForecastSync` lives in core because two features need it, while each feature owns its freshness rule.
- **Colors from one seed** ([ADR-0013](../adr/0013-brand-color-scheme-from-one-seed.md)): every role generated with MaterialKolor (Fidelity); dynamic color is opt-in on Android 12+.

## Tests added

54 tests on the JVM and 51 on the iOS simulator across all modules (including Phase 1).

| Test | Verifies |
|---|---|
| `SafeApiCallTest` | Success with unknown fields ignored; 503 → server error; missing field and invalid JSON → data error; I/O → network error; cancellation reports no error |
| `HttpClientFactoryTest` | 10 s / 15 s defaults; exceeding the request timeout → network error; logging installed only when requested |
| `FavoriteCityDaoTest` | Ordering and reactivity; saving twice keeps one entry; is-favorite transitions; a favorite gains its temperature |
| `ForecastDaoTest` | Current conditions stored; days ordered by date; a moved window drops old days; a repeated date does not fail |
| `DatabaseModuleTest` | The Koin module provides both DAOs |
| `TemperatureUnitTest`, `DataStoreUserPreferencesRepositoryTest` | Rounding and Fahrenheit conversion; Celsius default; persisted across a restart; unchanged value not re-emitted; unknown value read as Celsius |
| `OpenMeteoForecastApiTest` | Request URL and parameters; response parsing |
| `ForecastSyncTest` | Download stored with its time; last download time; a 503 keeps the cache; uneven daily lists keep the common days |
| `ThemeTest` | Light `primaryContainer` is `#1F3A93`; dark scheme differs; dynamic color off Android keeps the brand scheme |
| `StatesTest` | Retry invoked once; retry reachable by scrolling at 200 % font in a short window; loading announced to screen readers |
| `ErrorMessageTest`, `ErrorMessageLocaleTest` (JVM) | Every kind has a message; server codes share one; exact English and Spanish texts |

The English and Spanish texts are pinned only on the JVM, because only the JVM test host lets a test switch the locale at runtime.

## How to build and test

All shared tests, on the JVM and the iOS simulator:
```bash
./gradlew allTests
```

One module, JVM only:
```bash
./gradlew :core:weather:jvmTest
```

Compile every module for Android, iOS devices and the iOS simulator:
```bash
./gradlew compileAndroidMain compileKotlinIosArm64 compileKotlinIosSimulatorArm64
```

Validate the OpenSpec change:
```bash
openspec validate --changes --strict
```
