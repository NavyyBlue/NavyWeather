# Tasks

## 1. Toolchain and repository setup

- [x] 1.1 Add the Gradle 9.9.2 wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/`) and verify `./gradlew --version` reports Gradle 9.9.2
- [x] 1.2 Add `gradle/gradle-daemon-jvm.properties` with `toolchainVersion=25` and verify the running daemon uses a JDK 25 installation
- [x] 1.3 Add `.gitignore` (Gradle, Kotlin, IDE, Xcode, `local.properties`, secrets) and verify `git status` does not list `local.properties` or build outputs
- [x] 1.4 Add `gradle.properties` (configuration cache, build cache, parallel, AndroidX, suppressed host-test warning with its reason) and verify `./gradlew help` succeeds with the configuration cache stored

## 2. Version catalog and convention plugins

- [x] 2.1 Create `gradle/libs.versions.toml` with the full stack pinned to exact versions, Android SDK levels (compile 37, target 36, min 26) and plugin aliases; verify every entry resolves from Google Maven or Maven Central
- [x] 2.2 Create the `build-logic` included build with `navyweather.kmp.library` (android/iosArm64/iosSimulatorArm64/jvm targets, namespace from module path, kotlin-test and coroutines-test) and verify `./gradlew :build-logic:convention:build` succeeds
- [x] 2.3 Add `navyweather.kmp.compose`, `navyweather.kmp.feature` and `navyweather.room` convention plugins and verify `build-logic` compiles with all four registered
- [x] 2.4 Add root `settings.gradle.kts` (includes build-logic, repositories, typesafe project accessors) and root `build.gradle.kts` (plugins declared with `apply false`) and verify `./gradlew projects` lists the core modules

## 3. Core modules

- [x] 3.1 Create `core:common` with `AppResult`, `AppError`, `map`/`onSuccess`/`onFailure`, `DispatcherProvider` and `LanguageProvider` (Android, iOS, JVM actuals); verify `AppResultTest` covers mapping a success and leaving a failure unchanged (specs/error-handling)
- [x] 3.2 Create `core:mvi` with `UiState`/`UiIntent`/`UiEffect`, `Reducer`, `MviViewModel` and `CollectEffects`; verify a new `MviViewModelTest` proves state is updated before side effects run, the current state is available immediately, and each effect is delivered once (specs/presentation-architecture)
- [x] 3.3 Create `core:navigation` with `FavoritesRoute`, `SearchRoute` and `ForecastRoute`; verify a test serializes and deserializes `ForecastRoute` without losing arguments
- [x] 3.4 Create `core:testing` with `runViewModelTest` and `TestDispatcherProvider`; verify `MviViewModelTest` uses them to run with virtual time

## 4. Decisions and documentation

- [x] 4.1 Write ADRs 0002 (layers as packages), 0003 (in-house MVI contract), 0004 (convention plugins), 0005 (JVM test host), 0006 (Material 3 pre-release) and 0007 (JDK 25 daemon) from `docs/adr/template.md`, add them to the index in `docs/adr/README.md`, and verify `openspec validate --changes --strict` passes for this change
- [x] 4.2 Write `docs/architecture.md` (module topology diagram, layers as packages, dependency rules, MVI contract and MVI vs MVVM comparison, linking the ADRs for each decision) and verify every module it names exists or is marked with the phase that adds it
- [x] 4.3 Write `docs/tech-stack.md` (purpose and justification against alternatives for network, local data, UI/navigation, DI and tests, plus the Material 3 pre-release rationale, linking ADR-0006 and ADR-0007) and verify every library it lists is in the version catalog
- [x] 4.4 Write `docs/phases/phase-1-build-foundation.md` (scope, decisions, how to build and test, prerequisites) and update the README roadmap and links; verify the commands documented there run as written

## 5. Integration check

- [x] 5.1 Run `./gradlew allTests` and compile every core module for Android, iOS device and iOS simulator; verify all tests pass on the JVM and on the iOS simulator and all targets compile

## Workflow follow-up

- Open the Phase 1 pull request from `phase-1/build-foundation` to `main`.
- After review, run `/opsx:verify`, then `/opsx:archive` so the three capabilities become main specs in `openspec/specs/`.
