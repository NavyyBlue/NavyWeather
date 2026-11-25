# Design

## Context

Phases 1 and 2 delivered the build, the MVI contract, typed routes and every core module (network, cache, preferences, `ForecastSync`, design system). This change adds the first feature and the apps that host it (see proposal.md). Constraints:

- A feature never depends on another feature (ADR-0002), so favorites reaches the forecast and search screens only through `core:navigation` routes, and only `shared` knows every destination.
- Search and forecast arrive in Phase 4, so `shared` provides temporary placeholders for their routes.
- Requirements are in `specs/app-shell` and `specs/favorite-places`.

## Goals / Non-Goals

**Goals:**
- `feature:favorites` is a complete reference for the remaining features: layers as packages, a pure reducer, use cases for every business rule, and fakes in tests.
- The whole Koin graph and the navigation flow are verified by tests, not only by running the app.
- The apps run on the iOS simulator and on Android.

**Non-Goals:**
- Search, the real forecast screen, and saving favorites (Phase 4).
- Adaptive layouts beyond a readable maximum list width (640 dp).
- Release signing and store metadata.

## Decisions

| ADR | Decision |
|---|---|
| [0014](../../../docs/adr/0014-navigation-owned-by-the-composition-root.md) | Type-safe Navigation Compose. Each feature exposes one `NavGraphBuilder` entry point, and only `shared` builds the `NavHost`. Placeholders stand in for destinations of later phases |
| [0015](../../../docs/adr/0015-ios-static-framework-built-from-xcode.md) | iOS consumes `shared` as a static framework that Xcode builds through Gradle's `embedAndSignAppleFrameworkForXcode` |

### `feature:favorites`

```
feature/favorites/.../favorites/
├── domain/
│   ├── model/FavoriteCity            id, name, country?, lat, lon, temperatureCelsius?, weatherUpdatedAtEpochMillis?
│   ├── repository/FavoritesRepository observe, remove, save, refreshWeather
│   └── usecase/                      ObserveFavorites (sorting), ObserveTemperatureUnit, RemoveFavorite,
│                                     RestoreFavorite, RefreshFavoritesWeather (freshness rule)
├── data/
│   ├── mapper/                       FavoriteCityWithWeather ↔ FavoriteCity ↔ FavoriteCityEntity
│   └── repository/FavoritesRepositoryImpl   FavoriteCityDao + ForecastSync + Clock
├── presentation/                     FavoritesContract, FavoritesReducer, FavoritesViewModel, FavoritesScreen
├── navigation/FavoritesNavigation    NavGraphBuilder.favoritesScreen(onNavigateToForecast, onNavigateToSearch)
└── di/FavoritesModule
```

- **Sorting** is a product rule, so it lives in `ObserveFavoritesUseCase`, not in the DAO. A small folding table produces a case- and accent-insensitive key without a platform collator, and "ñ" sorts after every other "n", as in Spanish. Ties are broken by country.
- **Freshness**: `RefreshFavoritesWeatherUseCase(cities, force)` downloads the cities whose `weatherUpdatedAt` is missing or not within the last 10 minutes (a future timestamp counts as stale), or all of them when `force` is set. Downloads run in parallel with `async`. It returns the first error or `null`, and never throws.
- **Removal with undo**: `RemoveFavoriteUseCase` deletes the place and returns it. `RestoreFavoriteUseCase` saves it again. The ViewModel emits `ShowRemoved(city)`, and the destination shows the snackbar **in its own coroutine**, so a suspended `showSnackbar` never blocks the effect collector (navigation stays immediate).
- **ViewModel**:
  - It observes favorites and the unit.
  - On the first non-empty list it runs one background refresh. Pull to refresh is a forced refresh.
  - A running refresh job blocks a second one.
  - Only a forced refresh reports its error, through `ShowRefreshError`.
- **Screen**:
  - `LargeFlexibleTopAppBar` with title and subtitle, and a `Scaffold` with a `SmallExtendedFloatingActionButton`, shown only when the list is not empty.
  - `PullToRefreshBox` with a `LazyColumn` of segmented list items, at most 640 dp wide. All insets go to `contentPadding`, plus clearance for the FAB.
  - `SwipeToDismissBox` (end to start) on each row, with a matching custom accessibility action.
  - The temperature, or "–" with a content description, uses `tabularNumbers()`.
- `onNavigateToSearch` is nullable. `null` hides both search entry points, which keeps the feature usable without a search feature.

### `shared` (composition root)

- `App()` = `NavyWeatherTheme { NavHost(start = FavoritesRoute) { favoritesScreen(...); placeholderScreen<SearchRoute>(); placeholderScreen<ForecastRoute>() } }`. A placeholder is a small `Scaffold` with a back arrow, the route's title and "Available in a later phase", in English and Spanish. Phase 4 deletes the placeholders.
- `appModules(enableHttpLogging)` lists every module: platform primitives (`Clock.System`, `SystemLanguageProvider`), network, database, datastore, weather and favorites. `initKoin(enableHttpLogging, platformConfig)` starts Koin once.
- iOS: `MainViewController()` returns `ComposeUIViewController { App() }`, and `startKoinIos()` uses `Platform.isDebugBinary` for logging. The framework is static, with base name `Shared`.
- Tests (JVM):
  - `KoinGraphTest`: `verify()` on the whole graph, with `HttpClientEngine` and `SavedStateHandle` as extra types.
  - `AppNavigationTest`: starts Koin with test overrides (in-memory database seeded with a favorite, a `MockEngine` client, a temporary DataStore file). It checks that tapping a favorite opens the forecast placeholder with the right title, and that Back returns to favorites. It also checks that search opens its placeholder.

### `androidApp`

- An AGP application module (`com.navyblue.navyweather`, `minSdk 26`, `targetSdk 36`) with `buildConfig` enabled, and R8 plus resource shrinking for release.
- `NavyWeatherApplication` calls `initKoin(BuildConfig.DEBUG) { androidContext(this) }`.
- `MainActivity` calls `enableEdgeToEdge()` before `setContent { App() }`.
- Themes: `values` and `values-night` set `android:windowBackground` to the generated light and dark surface colors (`#FBF8FF` and its dark counterpart), so there is no flash at launch. There is also an adaptive launcher icon.

### `iosApp`

- An Xcode project with the `NavyWeather` scheme. `NavyWeatherApp` (SwiftUI) calls `MainViewControllerKt.startKoinIos()` in `init`. `ContentView` wraps `ComposeView` with `.ignoresSafeArea()`, because Compose handles insets itself.
- A Run Script build phase runs `cd "$SRCROOT/.." && ./gradlew :shared:embedAndSignAppleFrameworkForXcode`. `ENABLE_USER_SCRIPT_SANDBOXING = NO` lets the script reach the Gradle project.

### Phase 1 follow-up

`core:mvi` gets a `CollectEffectsTest` (a Compose UI test with a controllable `LifecycleOwner`). An effect sent while the lifecycle is CREATED is not delivered until the lifecycle reaches STARTED, and it is delivered exactly once.

## Risks / Trade-offs

- [Placeholders ship in an intermediate build] → they are clearly labeled, have a working back action, and are removed in Phase 4. Nothing outside `shared` refers to them.
- [The favorites list cannot be filled from the UI until Phase 4] → list, refresh and removal are covered by ViewModel and UI tests with fakes, and the navigation test seeds the database.
- [The Koin graph is verified only on the JVM (`verify()` uses reflection)] → the same modules are used on iOS, and the iOS app launch is checked on the simulator.
- [The Xcode build depends on a Gradle script] → the build phase is documented in the phase doc and ADR-0015, with the required sandboxing setting.
- [No Android emulator image is installed] → the Android app is built (`assembleDebug`), and runs on a connected device when one is available.

## Open Questions

- None blocking. Google Sans Flex is left for a separate decision.
