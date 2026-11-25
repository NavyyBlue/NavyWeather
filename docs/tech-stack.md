# Tech stack

Every library is pinned to an exact version in [`gradle/libs.versions.toml`](../gradle/libs.versions.toml), using the latest stable release at the time it was added, with one deliberate exception explained below (Material 3).

## Library justification

| Area | Selected | What it is for | Why it was chosen (vs. alternatives) |
|---|---|---|---|
| **Network (REST)** | **Ktor Client 3** (core, content negotiation, logging; **OkHttp** engine on Android, **Darwin** on iOS) + **kotlinx.serialization** | Coroutine-based HTTP client with the same code in `commonMain`. Each platform uses its native engine; Darwin uses `NSURLSession` and respects iOS network settings. kotlinx.serialization turns JSON into DTOs. | **Retrofit** is not multiplatform: it would mean two clients behind an interface, which is what KMP avoids. **Apollo** only fits GraphQL (Open-Meteo is REST). **Ktorfit** adds Retrofit-style annotations but brings KSP and one more dependency for a few endpoints. kotlinx.serialization does not use reflection, so it works on Kotlin/Native, unlike Gson or Moshi. Ktor's `MockEngine` tests data sources without a network. See [ADR-0008](adr/0008-ktor-client-with-native-engines.md). |
| **Local database** | **Room KMP** + **bundled SQLite** + **KSP**, plus **DataStore Preferences** | Room stores the offline forecast cache and favorites, with DAOs that return reactive `Flow`s. Bundled SQLite ships the same SQLite version on both platforms. DataStore keeps key-value preferences such as the temperature unit. | **SQLDelight** is the strongest alternative: longer KMP history and very type-safe SQL-first approach. Room was chosen because it is stable on KMP, maintained by Google, its API is already known to Android developers, and KSP validates queries at compile time. Bundled SQLite avoids differences with the system SQLite on iOS. **Realm Kotlin** is deprecated. A table for two or three preferences would be overkill, hence DataStore. See [ADR-0009](adr/0009-room-kmp-with-bundled-sqlite.md), [ADR-0010](adr/0010-datastore-for-preferences.md). |
| **UI, Material and navigation** | **Compose Multiplatform** + **Material 3 Expressive** + **MaterialKolor** + **Navigation Compose** (type-safe `@Serializable` routes) + **Compose Resources** + **lifecycle-viewmodel-compose** | One declarative UI for Android and iOS, with a shared design system. Navigation is type-safe: route arguments are checked at compile time. Compose Resources shares strings and images. MaterialKolor generates the full color scheme from one brand seed color. | **Native UI** (Compose + SwiftUI) feels more native on iOS but doubles the UI code, and the challenge asks to reuse UI. Compose Multiplatform is stable for iOS. **Navigation Compose** is the official, stable option; **Voyager** and **Decompose** bring their own lifecycle model and learning curve; **Navigation 3** is promising but its multiplatform support is still recent. **Material Theme Builder** would export static colors; MaterialKolor computes them from the seed, so a brand change is one line. See [ADR-0006](adr/0006-material-3-expressive-pre-release.md), [ADR-0013](adr/0013-brand-color-scheme-from-one-seed.md). |
| **Dependency injection** | **Koin 4** (core, compose, compose-viewmodel) | Creates and scopes HTTP clients, DAOs, repositories, use cases and ViewModels. Each feature exposes one module; `shared` assembles them. | No code generation, so no extra KSP time, which is noticeable in KMP. The most mature option in KMP, with direct ViewModel support in Compose. Its weak spot (graph errors at runtime) is covered by a `verify()` test of the whole graph. **kotlin-inject** and **Metro** check the graph at compile time but add code generation and complexity. **Kodein** has a smaller community. See [ADR-0011](adr/0011-koin-for-dependency-injection.md). |
| **Tests** | **kotlin.test**, **kotlinx-coroutines-test**, **Turbine**, **Mokkery**, **Compose UI Test**, **Ktor MockEngine**, in-memory Room, **Konsist** | Assertions written once and run on the JVM and iOS; virtual time for coroutines; step-by-step `Flow` assertions; mocks; UI tests from common code; architecture rules as tests. | **MockK** and **Mockito** rely on JVM reflection and do not run on Kotlin/Native; **Mokkery** is a compiler plugin that works on every target. Fakes are preferred for repositories (less brittle); Mokkery is used where a single stub is enough. **Turbine** avoids manual `delay`/`collect` and flaky tests. Reducers are pure functions tested with plain assertions. **Konsist** gives the layer guarantees that per-layer submodules would have given. |

## Cross-cutting libraries

| Library | Use | Why |
|---|---|---|
| kotlinx-coroutines | Concurrency in every layer | The Kotlin standard, and the base of Ktor, Room, Flow and MVI. |
| kotlinx-datetime | Forecast dates and times | `java.time` does not exist on iOS. |
| Kermit | Logging | Logcat on Android and OSLog on iOS behind one API. |

## Why a pre-release for Material 3

Recorded in [ADR-0006](adr/0006-material-3-expressive-pre-release.md).

Material 3 Expressive (spring-based motion, `LoadingIndicator`, flexible app bars, button groups, an extended shape scale) **only ships in pre-release builds**. The latest stable Material 3 for Compose Multiplatform predates Expressive and is older than the rest of the Compose 1.12 stack.

| Risk | Mitigation |
|---|---|
| Alpha APIs can change | The exact version is pinned and Expressive APIs are used only behind the design system module, so a change is fixed in one place. |
| Some APIs are experimental | Every use carries an explicit `@OptIn`, visible in code review. |
| Version drift with Compose | `material3 1.12.0-alpha03` is the line aligned with Compose Multiplatform 1.12, and MaterialKolor 5.0.1 is built against it. |

It also requires `compileSdk 37` (`targetSdk` stays at 36). When Expressive reaches a stable release, moving to it is a version change in the catalog.

## Toolchain

| Tool | Version | Note |
|---|---|---|
| Gradle | 9.9.2 | Through the wrapper. |
| JDK for the Gradle daemon | 25 (LTS) | Pinned in `gradle/gradle-daemon-jvm.properties` ([ADR-0007](adr/0007-gradle-daemon-on-jdk-25.md)). Only the build runs on it; app bytecode targets are set by AGP and Kotlin. |
| Kotlin | 2.4.21 | |
| Android Gradle Plugin | 9.5.3 | Multiplatform library plugin for shared modules. |
| Compose Multiplatform | 1.12.2 | |
| Android SDK | compile 37, target 36, min 26 | |
