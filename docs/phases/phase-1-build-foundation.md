# Phase 1 — Build foundation

**OpenSpec change:** [`phase-1-build-foundation`](../../openspec/changes/archive/2026-11-25-phase-1-build-foundation/) (archived; requirements now in [`openspec/specs/`](../../openspec/specs/))
**Capabilities:** `build-system`, `presentation-architecture`, `error-handling`
**ADRs:** [0002](../adr/0002-clean-architecture-layers-as-packages.md), [0003](../adr/0003-in-house-mvi-contract.md), [0004](../adr/0004-convention-plugins-in-build-logic.md), [0005](../adr/0005-jvm-target-as-test-host.md), [0006](../adr/0006-material-3-expressive-pre-release.md), [0007](../adr/0007-gradle-daemon-on-jdk-25.md)

## Goal

Create a reproducible multiplatform build and the primitives every later phase builds on, and write down the architecture and stack decisions before any feature exists.

## What this phase delivers

| Area | Delivered |
|---|---|
| Toolchain | Gradle 9.9.2 wrapper; Gradle daemon pinned to JDK 25 |
| Dependencies | Version catalog with the whole stack pinned to exact versions; `compileSdk 37`, `targetSdk 36`, `minSdk 26` |
| Build conventions | `build-logic` with `navyweather.kmp.library`, `.kmp.compose`, `.kmp.feature` and `.room` |
| Targets | `android`, `iosArm64`, `iosSimulatorArm64`, plus `jvm` as a test host only |
| `core:common` | `AppResult` / `AppError`, `DispatcherProvider`, `LanguageProvider` (Android, iOS, JVM) |
| `core:mvi` | `UiState` / `UiIntent` / `UiEffect`, `Reducer`, `MviViewModel`, `CollectEffects` |
| `core:navigation` | `FavoritesRoute`, `SearchRoute`, `ForecastRoute` |
| `core:testing` | `runViewModelTest`, `TestDispatcherProvider` |
| Decisions | ADRs 0002–0007 in [docs/adr](../adr/README.md) |
| Specs | The OpenSpec change for this phase |
| Docs | [architecture.md](../architecture.md), [tech-stack.md](../tech-stack.md), this document |

## Key decisions

Each one has its ADR with the alternatives considered.

- **Layers as packages, not submodules** ([ADR-0002](../adr/0002-clean-architecture-layers-as-packages.md)), to keep the number of KMP modules (and Kotlin/Native link time) low; boundaries are enforced by architecture tests later.
- **Convention plugins by role** ([ADR-0004](../adr/0004-convention-plugins-in-build-logic.md)), so a new module is one plugin line plus its own dependencies.
- **`jvm()` only as a test host** ([ADR-0005](../adr/0005-jvm-target-as-test-host.md)), so database, UI and DI tests run without an emulator.
- **In-house MVI contract** ([ADR-0003](../adr/0003-in-house-mvi-contract.md)) on the multiplatform androidx `ViewModel` instead of Orbit or MVIKotlin.
- **JDK 25 pinned without auto-download** ([ADR-0007](../adr/0007-gradle-daemon-on-jdk-25.md)), so a missing JDK fails with a clear message.

## Tests added

| Test | Verifies |
|---|---|
| `AppResultTest` | Mapping a success transforms it; mapping a failure leaves it unchanged without calling the mapper; a server error keeps its status code |
| `MviViewModelTest` | State is available immediately; background results come back as intents; reducer is deterministic; side effects run after the state update; effects are delivered once and in order |
| `RoutesTest` | `ForecastRoute` keeps all its arguments through serialization |

## Prerequisites

- JDK 25 (used by the Gradle daemon).
- Android SDK with platform 37, and `sdk.dir` in `local.properties`.
- macOS with Xcode and at least one iOS simulator, for iOS tests.

## How to build and test

All shared tests, on the JVM and the iOS simulator:
```bash
./gradlew allTests
```

JVM only (fastest):
```bash
./gradlew jvmTest
```

Compile the core modules for Android, iOS devices and the iOS simulator:
```bash
./gradlew compileAndroidMain compileKotlinIosArm64 compileKotlinIosSimulatorArm64
```

Validate the main specs:
```bash
openspec validate --specs --strict
```
