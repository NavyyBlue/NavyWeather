# Design

## Context

Phase 3 delivered favorites, the composition root with placeholder destinations, and both apps. This change adds the two remaining features and the brand typeface (see proposal.md). Constraints:

- No feature depends on another (ADR-0002). Search opens the forecast through `ForecastRoute`. The forecast saves favorites by writing the shared `favorite_cities` table, which favorites already observes.
- Navigation is owned by `shared` (ADR-0014), and the placeholders are replaced in place.
- Requirements are in `specs/city-search`, `specs/weather-forecast`, and the deltas for `app-shell`, `favorite-places` and `design-system`.

## Goals / Non-Goals

**Goals:**
- Both features follow the reference structure of `feature:favorites`: layers as packages, rules in use cases, a pure reducer, and fakes in tests.
- Every scenario is covered by a test on the JVM and the iOS simulator. Cross-feature behavior is covered by the navigation test of the real `App()`.
- The same typeface on both platforms, with its license shipped.

**Non-Goals:**
- Current location, deep links, more units, and widgets.
- Subsetting the font file. The full variable font is shipped, and its size is noted as a trade-off.

## Decisions

| ADR | Decision |
|---|---|
| [0016](../../../docs/adr/0016-search-as-you-type.md) | Search as you type: the use case trims the query and enforces the 2-character minimum. The ViewModel debounces for 350 ms and cancels the previous search job on every keystroke. Retry skips the debounce |
| [0017](../../../docs/adr/0017-adaptive-forecast-layout.md) | The forecast adapts by available width (`BoxWithConstraints`, at least 840 dp → two panes) instead of by device type |
| [0018](../../../docs/adr/0018-google-sans-flex-typeface.md) | Google Sans Flex (variable, SIL OFL 1.1) bundled as a Compose resource and used by every typography role |

### `feature:search`

- `data/remote/GeocodingApi`: `GET https://geocoding-api.open-meteo.com/v1/search` with `name`, `count=10`, `language` and `format=json`. `results` is absent when nothing matches, so it defaults to an empty list. `admin1` maps to `region`.
- `SearchRepositoryImpl` wraps the call in `safeApiCall` and maps DTO → `CitySearchResult`.
- `SearchCitiesUseCase(repository, languageProvider)` trims the query, returns an empty success below 2 characters without calling the repository, and passes the device language.
- `SearchViewModel`:
  - `QueryChanged` → the reducer stores the query. A query below the minimum clears results and the error, and leaves the idle hint. The previous job is cancelled and a new one waits `DEBOUNCE_MILLIS = 350`.
  - `RetryClicked` searches immediately.
  - `ResultsLoaded(query, results)` records `searchedQuery`, so "no results" shows the query that produced it.
  - Results stay in the state while a new search runs.
- `SearchScreen`: a top bar with back and a `TextField`, focused through a `FocusRequester` when it opens; a clear button; an idle hint (`EmptyState`); `LoadingIndicator` while searching with no results yet; a results `LazyColumn` with insets and IME padding in `contentPadding`; the "no results" state; and `ErrorState` with retry. The row subtitle is `listOfNotNull(region.takeIf { it != name }, country).joinToString(", ")`.
- `searchScreen(onNavigateToForecast, onNavigateBack)`.

### `feature:forecast`

- Domain:
  - `Location`.
  - `Forecast(current, daily, updatedAtEpochMillis)`. Dates and times are parsed with kotlinx-datetime as `LocalDate` and `LocalDateTime`, in the place's own time zone (the API uses `timezone=auto`).
  - `Temperature`, a value class in Celsius with `roundedIn(unit)`.
  - `WeatherCondition.fromWmoCode`.
- Repositories:
  - `ForecastRepositoryImpl(sync, dao)` combines `observeCurrent` and `observeDaily` into `Forecast?` (with `distinctUntilChanged`). Refreshing delegates to `ForecastSync`.
  - `FavoriteStatusRepositoryImpl(dao, clock)` observes and sets the favorite on `favorite_cities`.
- Use cases:
  - `RefreshForecastUseCase(repository, clock)` skips the download when the last download is within 10 minutes (a future timestamp counts as stale). `force` always downloads.
  - Observe forecast, observe favorite status, toggle favorite, and observe or set the unit.
- `ForecastViewModel(route, …)`:
  - The `ForecastRoute` comes from the navigation entry through Koin `parametersOf(route)`.
  - It observes the forecast, the favorite status and the unit, then runs a non-forced refresh.
  - `RefreshRequested` and `RetryClicked` force a refresh. A pending forced refresh runs after a running one, as in favorites.
  - On failure: with no cached data, the state gets `error`, which shows the full-screen error. With cached data, it emits `ShowRefreshError`.
  - `FavoriteToggled` flips the state optimistically, then persists.
- `ForecastScreen`:
  - A `MediumFlexibleTopAppBar` with back and a `FilledTonalIconToggleButton` heart, labeled "Add to favorites" or "Remove from favorites".
  - `PullToRefreshBox`, with loading, full-screen error and content.
  - The hero `Card` on `primaryContainer`: the temperature uses `BasicText` with `TextAutoSize.StepBased(40–96 sp)` and `maxLines = 1`, plus the unit, the condition, and high and low. The card merges its semantics into "22°C, Partly cloudy".
  - A °C/°F connected `ToggleButton` group on secondary roles; details tiles; and day rows with "Today" for the first date equal to the observed local date.
  - `BoxWithConstraints`: at 840 dp or wider, the current conditions and the days sit side by side. Below that, one scrolling column.
- `forecastScreen(onNavigateBack)` reads the route with `entry.toRoute<ForecastRoute>()` and passes it to `koinViewModel { parametersOf(route) }`.

### `shared`

- `App()`:
  - `searchScreen(onNavigateToForecast = { navigate(route) { popUpTo<SearchRoute> { inclusive = true } } }, onNavigateBack = popBackStack)`.
  - `forecastScreen(onNavigateBack = popBackStack)`.
  - `PlaceholderScreen` and its strings are deleted.
- `appModules()` adds `searchModule` and `forecastModule`. `KoinGraphTest` adds `ForecastRoute` as an extra runtime type.
- `AppNavigationTest`, with a `MockEngine` that answers geocoding and forecast requests:
  - favorites → forecast (real screen) → Back → favorites;
  - favorites → search → type "Lima" → pick → forecast → Back → favorites;
  - save from the forecast → Back → the place is in favorites.

### Typeface (`core:designsystem`)

- `composeResources/font/google_sans_flex.ttf` is the variable font, renamed to a valid resource name. `composeResources/files/OFL.txt` carries the license.
- `NavyTypography` becomes `@Composable fun navyTypography(): Typography`. It builds a `FontFamily` from the variable font, with instances for weights 400, 500, 600 and 700 (`FontVariation.Settings(FontVariation.weight(w))`), and copies Material's default type scale onto that family.
- `NavyWeatherTheme` passes it to `MaterialExpressiveTheme`. `tabularNumbers()` is kept, because Google Sans Flex supports `tnum`.
- A test asserts that every typography role uses the Google Sans Flex family.

## Risks / Trade-offs

- [The font adds about 3.9 MB to each app] → accepted for a consistent brand. Subsetting to Latin can come later without code changes.
- [Variable font axes are not honored on every renderer] → weights are created as explicit `Font` instances with variation settings. Compose supports these on Android API 26+ (the minimum) and Skia (iOS, JVM). Both apps are checked by hand.
- [The "Google Sans" name is a trademark] → the font is used unmodified under OFL with its license, and the app does not claim affiliation.
- [Open-Meteo geocoding returns names in the requested language only when available] → the API falls back to English names. The spec asks for the request language, not for translated results.
- [The ViewModel takes a navigation argument] → it is passed explicitly through `parametersOf`, so tests build it directly with a route.

## Open Questions

- None.
