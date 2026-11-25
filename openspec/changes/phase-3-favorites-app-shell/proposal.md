# Proposal

## Why

After Phase 2 every core building block exists, but there is still no app to open: no feature, no composition root and no Android or iOS entry point. This phase delivers the first feature end to end, favorite places as the home screen, inside real Android and iOS apps. That proves the whole stack together: Clean Architecture layers in a feature, MVI, Koin wiring, offline-first data, navigation and the shared UI on both platforms.

## What Changes

- `feature:favorites`, with `domain`, `data`, `presentation` and `di` packages:
  - Domain model and repository interface.
  - Use cases: observe favorites, sorted by name and country ignoring case and accents; observe the temperature unit; remove and restore a favorite; refresh stale temperatures in parallel with a **10-minute freshness rule**, where pull to refresh downloads all of them.
  - A repository on top of `core:database` and `ForecastSync`.
  - An MVI contract, reducer and ViewModel.
  - The Material 3 Expressive screen: large flexible app bar, empty state, list with temperatures, pull to refresh, swipe to remove with undo (also as an accessibility action), and a search FAB.
  - Strings in English and Spanish.
- `shared`, the composition root:
  - `App()` with the theme and a `NavHost` over typed routes.
  - Koin startup with every module, plus the app-wide `Clock` and `LanguageProvider`.
  - The iOS framework and its `MainViewController`.
  - **Temporary placeholder destinations** for search and forecast. They show a short "coming in a later phase" screen, so navigation and back behavior are complete and testable now. Phase 4 replaces them with the real screens.
- `androidApp`: a single activity (edge to edge), an application class that starts Koin with HTTP logging only in debug builds, launcher icon, and themes whose window background matches the app surface in light and dark.
- `iosApp`: an Xcode project with a SwiftUI app hosting the shared UI. It builds the shared framework through Gradle and starts Koin once, with HTTP logging only in debug binaries.
- Tests: favorites use cases, reducer, ViewModel and screen (JVM and iOS simulator); verification of the whole Koin graph and the navigation flow (JVM). Plus the open item from Phase 1: a test that effects are collected only while the screen is at least started.
- ADRs 0014–0015, architecture and phase docs, and the README roadmap.

Out of scope:
- City search and the real forecast screen, including saving a favorite from the forecast (Phase 4). Until then the app can show favorites only if they already exist in the database, so the list is exercised through tests.
- The Google Sans Flex typeface. It is an optional visual change that needs a font download, so it is left for a separate decision.
- Konsist architecture tests and the AI usage report (Phase 5).

## Capabilities

### New Capabilities
- `app-shell`: how the shared UI is hosted on Android and iOS and how screens connect. Typed routes and back behavior, edge-to-edge drawing, device language, light and dark themes without a launch flash, and debug-only request logging.
- `favorite-places`: the home screen with saved places. Alphabetical order, temperature in the user's unit, refreshing stale temperatures, opening a forecast, and removal with undo.

### Modified Capabilities
<!-- None: the Phase 1 "effects only while visible" scenario gets its missing test, but its requirement does not change. -->

## Impact

- New modules: `feature:favorites`, `shared`, `androidApp` (Android application), and the `iosApp` Xcode project, all registered in `settings.gradle.kts`.
- First use of Navigation Compose, Koin Compose and Koin Android, the AGP application plugin and the Kotlin/Native framework binary. All of them are already pinned in the catalog.
- `navyweather.kmp.feature` gets its first consumer.
- New docs: `docs/adr/0014`–`0015` and `docs/phases/phase-3-favorites-app-shell.md`. `docs/architecture.md` drops its Phase 3 markers.
- First runnable app. Checked on the iOS simulator, and on an Android device or emulator when one is available.
