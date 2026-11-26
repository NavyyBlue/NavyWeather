# Phase 4 — Search and forecast

**OpenSpec change:** `phase-4-search-forecast`
**Capabilities:** `city-search`, `weather-forecast` (new); `app-shell`, `favorite-places`, `design-system` (changed)
**ADRs:** [0016](../adr/0016-search-as-you-type.md), [0017](../adr/0017-adaptive-forecast-layout.md), [0018](../adr/0018-google-sans-flex-typeface.md)

## Goal

Complete the product: find a city, see its weather offline-first, save it as a favorite, and give the shared UI the same typeface on Android and iOS.

## What this phase delivers

| Part | Delivered |
|---|---|
| `feature:search` | Open-Meteo Geocoding (10 results in the device language); 2-character minimum; 350 ms debounce that cancels the previous search; immediate retry; focused search field; "region, country" subtitles without repeating the name; no-results and error states |
| `feature:forecast` | Offline-first repository on `ForecastSync` with the 10-minute reuse rule; hero card with a one-line auto-sized temperature announced as one element; feels like, humidity, wind, update time; 7 days with "Today"; °C/°F toggle persisted app-wide; favorite toggle writing the shared table; pull to refresh; full-screen error without cache and snackbar with cache; two panes at ≥ 840 dp |
| `shared` | Real search and forecast destinations (placeholders deleted); search popped when it opens a forecast; both modules in the Koin graph |
| `core:designsystem` | Google Sans Flex (variable, SIL OFL 1.1) for every type role, license and trademark note bundled; public `ERROR_STATE_RETRY_TAG` for UI tests |
| Docs | ADRs 0016–0018, architecture, tech stack, this document |

## Key decisions

- **Search as you type** ([ADR-0016](../adr/0016-search-as-you-type.md)): rules in the use case, timing in the ViewModel with a cancellable job.
- **Layout by available width** ([ADR-0017](../adr/0017-adaptive-forecast-layout.md)): two panes at 840 dp, not by device type.
- **Google Sans Flex** ([ADR-0018](../adr/0018-google-sans-flex-typeface.md)): one variable font for both platforms, used unmodified under OFL.
- **Features stay independent**: search opens the forecast through `ForecastRoute`; the forecast saves favorites by writing `favorite_cities`, which favorites already observes.
- **Same refresh rules everywhere**: the forecast uses the 10-minute rule and the same queued forced refresh as favorites.

## Bugs found while testing

- **Search field focus**: the field lives in the `Scaffold`'s top bar, which `Scaffold` composes separately, so a focus request from outside it ran before the field existed and was ignored. The request now runs inside the top bar slot; the new `SearchScreenTest` failed before the fix and passes after it, and the keyboard now opens on the phone.

## Tests added

137 tests on the JVM and 130 on the iOS simulator across all modules.

| Test | Verifies |
|---|---|
| `GeocodingApiTest`, `SearchCitiesUseCaseTest` | Request parameters and language; missing results mean none; server errors; short and whitespace queries skip the network |
| `SearchReducerTest`, `SearchViewModelTest` | One search for fast typing; nothing before the delay; results kept while refining; clearing to the idle hint; empty results; failure; immediate retry; navigation with the full route |
| `SearchScreenTest` | Focused field with the idle hint; region and country without repeating the name; no-results text with the query; retry |
| `TemperatureAndConditionTest`, `RefreshForecastUseCaseTest`, `ForecastRepositoryImplTest` | Rounding and °F; every WMO group and unknown code; 10-minute reuse and forced refresh; cache written and failures keep it |
| `ForecastReducerTest`, `ForecastViewModelTest` | Loading, errors with and without cache, retry, optimistic favorite, unit saved, pull during the opening refresh runs after it |
| `ForecastScreenTest`, `ForecastScreenDetailsTest` | Hero "22" with °C, condition and range, announced as one element; 7 days with "Today"; °F everywhere; favorite toggle labels; "Unknown conditions"; side by side at 1000 dp, one column at 400 dp |
| `TypographyTest` | Every role, including emphasized ones, uses Google Sans Flex |
| `AppNavigationTest` (JVM) | Favorites → forecast → Back; favorites → search → forecast → Back lands on favorites; search → Back; a place saved from its forecast appears in favorites |

The apps were also run by hand on an Android 16 phone (debug and the R8 release build) and on the iOS simulator: search "Lima" in Spanish, open "Lima, Perú", switch to °F, save it, Back to favorites with both temperatures in °F. A DNS failure on the phone's network also exercised the connection error and the immediate retry.

## How to build and run

All shared tests:
```bash
./gradlew allTests
```

Android app on a connected device or emulator:
```bash
./gradlew :androidApp:installDebug
```

iOS app: open `iosApp/iosApp.xcodeproj`, choose the `NavyWeather` scheme and a simulator, and press Run.

Validate the OpenSpec change:
```bash
openspec validate --changes --strict
```
