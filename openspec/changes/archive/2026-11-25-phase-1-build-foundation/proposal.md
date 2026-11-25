# Proposal

## Why

NavyWeather has a plan (README) but no code yet. Before any feature can be built, the project needs a reproducible multiplatform build, the architectural primitives every feature will share (result/error model, MVI contract, typed routes, test helpers) and the documents that justify those decisions, so that later phases only add features on top of agreed foundations.

## What Changes

- Gradle 9.9.2 wrapper, with the Gradle daemon pinned to JDK 25.
- `gradle/libs.versions.toml` with the complete selected stack pinned to exact versions (Kotlin 2.4.21, AGP 9.5.3, Compose Multiplatform 1.12.2, Material 3 1.12.0-alpha03, Ktor 3.6.2, Room 2.8.5, Koin 4.2.2, Mokkery 3.6.0 and the rest), `compileSdk 37`, `targetSdk 36`, `minSdk 26`.
- `build-logic` included build with convention plugins: `navyweather.kmp.library`, `navyweather.kmp.compose`, `navyweather.kmp.feature`, `navyweather.room`.
- Targets for every shared module: `android` (AGP 9 multiplatform library plugin), `iosArm64`, `iosSimulatorArm64`, and `jvm` used only as a fast test host.
- Base core modules:
  - `core:common`: typed result and error model, injectable dispatchers, device language provider.
  - `core:mvi`: MVI contract (state, intent, effect, pure reducer) and a ViewModel base that applies it.
  - `core:navigation`: typed, serializable route contracts for the three planned screens.
  - `core:testing`: helpers to test ViewModels with virtual time.
- ADRs 0002–0007 in `docs/adr/` for the decisions this phase makes: layers as packages, the in-house MVI contract, convention plugins, the JVM test host, the Material 3 pre-release and the JDK 25 daemon.
- Documentation: `docs/architecture.md`, `docs/tech-stack.md`, `docs/phases/phase-1-build-foundation.md`, and the README roadmap updated.

Out of scope (later phases): network, local database, preferences, shared weather data source and design system (Phase 2); features and the Android/iOS apps (Phases 3–4); architecture tests with Konsist and the AI usage report (Phase 5).

## Capabilities

### New Capabilities
- `build-system`: how the project is built and tested: pinned toolchain and versions, supported targets, convention plugins, and the commands that build every target and run shared tests on the JVM and the iOS simulator.
- `presentation-architecture`: the MVI contract every screen follows: single immutable state, intents as the only input, pure reducer transitions, side effects after the state update, and one-shot effects.
- `error-handling`: how operations report success or failure as typed values instead of exceptions, with a closed set of error kinds that later phases map to user-facing messages.

### Modified Capabilities
<!-- None: there are no existing specs yet. -->

## Impact

- New files only: Gradle wrapper and settings, version catalog, `build-logic/`, `core/common`, `core/mvi`, `core/navigation`, `core/testing`, `docs/` (including `docs/adr/`); the README roadmap is updated.
- Developer prerequisites introduced: JDK 25 for the Gradle daemon, Android SDK platform 37, Xcode with an iOS simulator for iOS tests.
- No user-facing behavior yet; the app itself starts in Phase 3.
