# Tasks

## 1. Favorites domain and data (`feature:favorites`)

- [x] 1.1 Create `feature:favorites` (feature convention) with `FavoriteCity`, `FavoritesRepository`, mappers and `FavoritesRepositoryImpl` (DAO + `ForecastSync` + `Clock`); verify it compiles for every target
- [x] 1.2 Add `ObserveFavoritesUseCase` with the accent- and case-insensitive sort; verify `ObserveFavoritesUseCaseTest` covers case-insensitive order, the accented example (Ávila, Nazca, Ñuñoa, Oruro, Zaragoza) and same names ordered by country (specs/favorite-places)
- [x] 1.3 Add `RefreshFavoritesWeatherUseCase` (10-minute freshness, parallel downloads, force) and the remove/restore/unit use cases; verify `RefreshFavoritesWeatherUseCaseTest` covers only stale or missing places downloaded, force downloading all, and failures returned without throwing (specs/favorite-places)

## 2. Favorites presentation

- [x] 2.1 Add `FavoritesContract`, `FavoritesReducer` and `FavoritesViewModel` (observe, first-list background refresh, forced refresh, single refresh at a time, remove/undo, navigation effects); verify `FavoritesReducerTest` and `FavoritesViewModelTest` cover loading then sorted list, remove offering undo, undo restoring, opening downloading stale temperatures, a failed pull to refresh showing an error, a failed background refresh staying silent, and a click navigating with the full forecast route (specs/favorite-places)
- [x] 2.2 Add `FavoritesScreen`, `FavoritesDestination`, `favoritesScreen()` navigation entry and English/Spanish strings; verify `FavoritesScreenTest` (JVM and iOS) covers the empty state with its search button and no FAB, rows in the selected unit, the dash with its "Temperature not available yet" description, the FAB with a list, row click, and removal through the accessibility action (specs/favorite-places)
- [x] 2.3 Verify the snackbar does not block navigation: a test shows a "removed" snackbar and confirms a row click is delivered while it is visible (specs/favorite-places)

## 3. Composition root (`shared`)

- [x] 3.1 Create `shared` with `App()` (theme + `NavHost` with favorites and placeholder destinations for search and forecast), `appModules()` / `initKoin()` with `Clock` and `LanguageProvider`, and the iOS static framework with `MainViewController()` / `startKoinIos()`; verify it compiles for every target
- [x] 3.2 Verify `KoinGraphTest` resolves every definition of `appModules()` on the JVM (specs/app-shell)
- [x] 3.3 Verify `AppNavigationTest` (JVM, Koin with test overrides and a seeded favorite) covers tapping a favorite opening the forecast placeholder with that place, Back returning to favorites, and search opening its placeholder with a way back (specs/app-shell)

## 4. Apps

- [x] 4.1 Create `androidApp` (application, edge to edge, Koin with `BuildConfig.DEBUG` logging, launcher icon, light/dark window backgrounds matching the surface, R8 for release); verify `./gradlew :androidApp:assembleDebug assembleRelease` succeeds and the app opens on the favorites screen on a device when one is connected (specs/app-shell)
- [x] 4.2 Create the `iosApp` Xcode project (SwiftUI host, Koin start, Gradle build phase, `NavyWeather` scheme); verify it builds for the simulator and opens on the favorites empty state, in light and dark mode and with the device in Spanish (specs/app-shell)

## 5. Phase 1 follow-up

- [x] 5.1 Verify `CollectEffectsTest` in `core:mvi`: an effect sent while the lifecycle is CREATED is delivered once when it reaches STARTED and not before (specs/presentation-architecture)

## 6. Decisions and documentation

- [x] 6.1 Write ADRs 0014 (navigation owned by the composition root, with placeholders) and 0015 (iOS static framework built from Xcode) and add them to `docs/adr/README.md`
- [x] 6.2 Update `docs/architecture.md` (remove the Phase 3 markers, describe the feature entry point and the composition root) and write `docs/phases/phase-3-favorites-app-shell.md` (deliverables, decisions, tests, how to run on Android and iOS); update the README roadmap and Getting started with how to run the apps

## 7. Integration check

- [x] 7.1 Register the new modules, run `./gradlew allTests`, compile every module for every target, build both apps, and verify `openspec validate --changes --strict` passes
