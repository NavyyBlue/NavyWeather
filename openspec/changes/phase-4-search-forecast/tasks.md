# Tasks

## 1. City search (`feature:search`)

- [ ] 1.1 Create `feature:search` with `GeocodingApi` and DTOs, `SearchRepositoryImpl`, `CitySearchResult` and `SearchCitiesUseCase`; verify `GeocodingApiTest` (MockEngine: name, count 10, language, missing `results` means no matches, 503 → server error) and `SearchCitiesUseCaseTest` (short and whitespace queries skip the network, trimmed query sent in the device language) (specs/city-search)
- [ ] 1.2 Add `SearchContract`, `SearchReducer` and `SearchViewModel` (350 ms debounce with cancellation, immediate retry, `searchedQuery`); verify `SearchReducerTest` and `SearchViewModelTest` cover typing fast searching once for the last query, no request before the delay, results kept while refining, clearing back to the idle hint, empty results, failure clearing results, immediate retry and a click navigating with the full route (specs/city-search)
- [ ] 1.3 Add `SearchScreen`, `SearchDestination`, `searchScreen()` and English/Spanish strings; verify `SearchScreenTest` (JVM and iOS) covers the focused field with the idle hint, result rows with "region, country" and the region omitted when it equals the name, the "no cities found" text with the query, and the error state's retry (specs/city-search)

## 2. Weather forecast (`feature:forecast`)

- [ ] 2.1 Create `feature:forecast` domain (`Location`, `Forecast`, `Temperature`, `WeatherCondition`), mappers, `ForecastRepositoryImpl`, `FavoriteStatusRepositoryImpl` and use cases; verify `TemperatureAndConditionTest` (rounding, Fahrenheit, every WMO group and unknown code 42), `RefreshForecastUseCaseTest` (fresh cache skips, stale and missing download, force downloads) and `ForecastRepositoryImplTest` (refresh caches and observers get the domain forecast, failed refresh keeps the cache) (specs/weather-forecast)
- [ ] 2.2 Add `ForecastContract`, `ForecastReducer` and `ForecastViewModel` (route via `parametersOf`, offline-first refresh, forced refresh queued after a running one, optimistic favorite, unit persistence); verify `ForecastReducerTest` and `ForecastViewModelTest` cover initial loading, download without cache, failure without cache as full-screen error, failure with cache as snackbar, retry back to loading, favorite toggle persisted both ways and unit selection saved (specs/weather-forecast)
- [ ] 2.3 Add `ForecastScreen` (hero with one-line auto-size temperature and merged semantics, details, 7-day list with "Today", °C/°F toggle, favorite toggle, pull to refresh, two panes at ≥ 840 dp), `forecastScreen()` and English/Spanish strings; verify `ForecastScreenTest` (JVM and iOS) covers the hero "22" with "°C", "Partly cloudy" and "High 24° · Low 16°", the hero announced as one element, 7 day rows with "Today" first, °F switching every temperature, the favorite toggle's label and action, "Unknown conditions" for code 42, and the side-by-side layout at 1000 dp versus one column at 400 dp (specs/weather-forecast)

## 3. Composition root

- [ ] 3.1 Replace the placeholders in `App()` with `searchScreen()` (search popped when it opens a forecast) and `forecastScreen()`, delete `PlaceholderScreen` and its strings, and add both modules to `appModules()`; verify `KoinGraphTest` passes with `ForecastRoute` as an extra type (specs/app-shell)
- [ ] 3.2 Extend `AppNavigationTest` (MockEngine answering geocoding and forecast): favorites → forecast → Back → favorites, favorites → search → pick → forecast → Back → favorites, search → Back → favorites, and saving a place from its forecast making it appear in favorites (specs/app-shell, specs/favorite-places)

## 4. Typeface

- [ ] 4.1 Download Google Sans Flex from the google/fonts repository, confirm `OFL.txt`, add `font/google_sans_flex.ttf` and `files/OFL.txt` to `core:designsystem`, and build `navyTypography()` on it with weights 400–700; verify a `TypographyTest` asserts every role uses the Google Sans Flex family and the theme still passes its existing tests (specs/design-system)

## 5. Apps

- [ ] 5.1 Build and run both apps: on the Android device (when connected) and the iOS simulator, search "Lima", open the forecast, toggle °F, save it, go back to favorites, and check the typeface; verify debug and release Android builds and the iOS simulator build succeed (specs/app-shell, specs/weather-forecast, specs/city-search)

## 6. Decisions and documentation

- [ ] 6.1 Write ADRs 0016 (search as you type), 0017 (adaptive forecast layout) and 0018 (Google Sans Flex) and add them to `docs/adr/README.md`
- [ ] 6.2 Update `docs/architecture.md` (remove Phase 4 markers and placeholder notes), `docs/tech-stack.md` (typeface), write `docs/phases/phase-4-search-forecast.md`, and update the README roadmap

## 7. Integration check

- [ ] 7.1 Register the new modules, run `./gradlew allTests`, compile every module for every target, and verify `openspec validate --changes --strict` passes
