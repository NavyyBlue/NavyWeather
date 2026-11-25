# Design

## Context

The repository only contains the README. This change creates the build and the shared primitives that Phases 2–5 rely on (see proposal.md - Why). Constraints:

- Android and iOS from one Kotlin codebase; the iOS app and its framework arrive in Phase 3.
- AGP 9 no longer supports `com.android.library` together with the Kotlin Multiplatform plugin; shared modules must use `com.android.kotlin.multiplatform.library`, and the Android app must be a separate module.
- Material 3 Expressive (chosen for the design system in Phase 2) requires `compileSdk 37`.
- Requirements are in `specs/build-system`, `specs/presentation-architecture` and `specs/error-handling`.

## Goals / Non-Goals

**Goals:**
- Any new module needs one plugin line plus its own dependencies.
- Shared tests run fast on the JVM and also on the iOS simulator from the first commit.
- The architecture and stack decisions are written down before features exist.

**Non-Goals:**
- Continuous integration and static analysis (Detekt/ktlint) are not set up here.
- Architecture rules are documented but not enforced yet (Konsist arrives in Phase 5).

## Decisions

The decisions with lasting architectural impact are recorded as ADRs, written in this change; this section summarizes them and keeps the details that only matter for implementing this phase.

| ADR | Decision |
|---|---|
| [0002](../../../../docs/adr/0002-clean-architecture-layers-as-packages.md) | Clean Architecture layers as packages inside each feature, not per-layer submodules |
| [0003](../../../../docs/adr/0003-in-house-mvi-contract.md) | MVI through an in-house contract on the multiplatform androidx `ViewModel` |
| [0004](../../../../docs/adr/0004-convention-plugins-in-build-logic.md) | Convention plugins by module role in an included `build-logic` build |
| [0005](../../../../docs/adr/0005-jvm-target-as-test-host.md) | A `jvm()` target used only as the shared test host |
| [0006](../../../../docs/adr/0006-material-3-expressive-pre-release.md) | Material 3 Expressive pre-release pinned in the catalog |
| [0007](../../../../docs/adr/0007-gradle-daemon-on-jdk-25.md) | Gradle daemon pinned to JDK 25 without auto-download |

### Implementation notes for these decisions
- Convention plugins: `navyweather.kmp.library` (targets, namespace, coroutines, tests), `navyweather.kmp.compose` (Compose, Material 3, Compose Resources), `navyweather.kmp.feature` (core modules, Koin, navigation, serialization, Mokkery and UI test dependencies) and `navyweather.room` (KSP + Room for every target). The Android namespace is derived from the Gradle path (`:core:mvi` → `com.navyblue.navyweather.core.mvi`).
- MVI: `onIntent` applies the reducer synchronously, then calls `handleIntent` for side effects (satisfies "side effects run after the state update"). Effects go through a buffered `Channel` consumed with `receiveAsFlow()`, so each effect is delivered once and never replayed; `CollectEffects` collects only while the lifecycle is at least STARTED.
- JDK: only `toolchainVersion=25` is declared in `gradle/gradle-daemon-jvm.properties`.

### Typed results in `core:common`
`AppResult<T>` (`Success` / `Failure`) and a sealed `AppError` (`Network`, `Http(code)`, `Serialization`, `Unknown(message)`), plus `map`, `onSuccess`, `onFailure`. Mapping from Ktor/kotlinx.io exceptions is added with the network module in Phase 2.

### Typed routes in `core:navigation`
`@Serializable` `FavoritesRoute`, `SearchRoute` and `ForecastRoute(cityId, name, country, latitude, longitude)`. Arguments are primitives so no feature has to expose its domain models to others. Behavior that uses them is specified with the app shell in Phase 3.

### Small cross-cutting providers
`DispatcherProvider` (injectable dispatchers for tests) and `LanguageProvider` (device language through `expect`/`actual` on Android, iOS and JVM), both interfaces so tests can replace them.

### Documentation split
`README.md` stays short; decisions go to `docs/adr/`; `docs/architecture.md` (topology, layers, dependency rules, MVI), `docs/tech-stack.md` (library justification table against alternatives) and `docs/phases/phase-1-build-foundation.md` (what this phase delivered and how to verify it).

## Risks / Trade-offs

- [Material 3 Expressive is a pre-release (1.12.0-alpha03)] → exact version pinned; Expressive APIs will only be used behind `core:designsystem` (Phase 2).
- [Very recent toolchain versions (Kotlin 2.4, AGP 9.5, Gradle 9.9) may have rough edges] → versions are pinned and the whole build is verified on all targets in this phase.
- [AGP warns that Android host tests are disabled] → intentional (JVM is the test host); the warning is suppressed with an explanatory comment in `gradle.properties`.
- [iOS tests need a simulator device] → documented as a prerequisite in the phase doc and the README.
