# Phase 3 — Favorite places and app shell

**OpenSpec change:** [`phase-3-favorites-app-shell`](../../openspec/changes/archive/2026-11-25-phase-3-favorites-app-shell/) (archived; requirements now in [`openspec/specs/`](../../openspec/specs/))
**Capabilities:** `app-shell`, `favorite-places`
**ADRs:** [0014](../adr/0014-navigation-owned-by-the-composition-root.md), [0015](../adr/0015-ios-static-framework-built-from-xcode.md)

## Goal

Deliver the first feature end to end inside real Android and iOS apps, proving the whole stack together: Clean Architecture layers in a feature, MVI, Koin, offline-first data, navigation and the shared UI on both platforms.

## What this phase delivers

| Part | Delivered |
|---|---|
| `feature:favorites` | Domain model, repository and use cases (accent-insensitive sorting, 10-minute freshness refresh in parallel, remove/restore, unit); repository on the DAO and `ForecastSync`; MVI contract, reducer, ViewModel; Material 3 Expressive screen with pull to refresh, swipe to remove with undo (and an accessibility action), search FAB; English and Spanish strings |
| `shared` | `App()` with the theme and the `NavHost`; placeholder destinations for search and forecast until Phase 4; `appModules()` / `initKoin()` with `Clock` and `LanguageProvider`; static iOS framework with `MainViewController()` and `startKoinIos()` |
| `androidApp` | Single activity, edge to edge, Koin with HTTP logging in debug only, light/dark window backgrounds matching the surface, launcher icon, R8 for release |
| `iosApp` | SwiftUI host, Koin start, Gradle build phase, `NavyWeather` scheme |
| Build | `kotlinx-coroutines-swing` in JVM UI tests (a `Dispatchers.Main` for ViewModels in Compose tests) |
| Docs | ADRs 0014–0015, architecture (feature entry points and composition root), README (how to run the apps), this document |

## Key decisions

- **Navigation owned by `shared`** ([ADR-0014](../adr/0014-navigation-owned-by-the-composition-root.md)): typed routes, one entry point per feature, navigation as an MVI effect, placeholders for screens of later phases.
- **Static iOS framework built from Xcode** ([ADR-0015](../adr/0015-ios-static-framework-built-from-xcode.md)): Run in Xcode always builds the current Kotlin code.
- **Freshness in the feature's domain**: `RefreshFavoritesWeatherUseCase` decides what is stale (missing or older than 10 minutes); `ForecastSync` in core only downloads.
- **Snackbars never block navigation**: the destination shows them in their own coroutine, so the effect collector keeps running.

## Tests added

90 tests on the JVM and 85 on the iOS simulator across all modules.

| Test | Verifies |
|---|---|
| `ObserveFavoritesUseCaseTest` | Case-insensitive order; Ávila, Nazca, Ñuñoa, Oruro, Zaragoza; same names ordered by country |
| `RefreshFavoritesWeatherUseCaseTest` | Only stale or missing places downloaded; force downloads all; failures returned, not thrown |
| `FavoritesReducerTest` | Loaded list stops loading; removal hides the row; navigation intents do not change state |
| `FavoritesViewModelTest` | Loading then sorted list; remove offers undo; undo restores; opening refreshes stale temperatures; failed pull to refresh shows an error; failed background refresh is silent; one refresh at a time; a pull to refresh during a background refresh runs right after it; click navigates with the full route |
| `FavoritesScreenTest` | Empty state with one search button and no FAB; FAB with a list; temperature in the selected unit; dash announced as "Temperature not available yet"; row click and removal via the accessibility action |
| `SnackbarDoesNotBlockTest` | A favorite tapped while the "removed" snackbar is visible opens immediately |
| `KoinGraphTest` (JVM) | Every definition of `appModules()` resolves |
| `AppNavigationTest` (JVM) | Real `App()` with a seeded database: favorite → forecast placeholder with that place → Back → favorites; search placeholder with a way back |
| `CollectEffectsTest` | An effect sent while CREATED is delivered once at STARTED and not again after returning from the background (Phase 1 follow-up) |

The apps were also run by hand: on an Android phone (Android 16) and on the iOS simulator, in Spanish, in light and dark mode, through favorite → forecast placeholder → back and search → placeholder → back. The R8 release build was installed on the phone (signed with the debug key) and went through the same flow, confirming no keep rule is missing.

## How to build and run

All shared tests:
```bash
./gradlew allTests
```

Android app on a connected device or emulator:
```bash
./gradlew :androidApp:installDebug
```

iOS app: open `iosApp/iosApp.xcodeproj`, choose the `NavyWeather` scheme and a simulator, and press Run. From the command line:
```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme NavyWeather -destination 'platform=iOS Simulator,name=iPhone 17 Pro' build
```

Validate the main specs:
```bash
openspec validate --specs --strict
```
