# Proposal

## Why

After Phase 3 the app runs on both platforms, but search and forecast are placeholders, so a user cannot find a city, see its weather or save a favorite. This phase delivers the two remaining features, which completes the product described in the README. It also replaces the system font with Google Sans Flex, so the forecast's hero number and the titles look the same on Android and iOS.

## What Changes

- `feature:search`:
  - Open-Meteo Geocoding client: up to 10 results in the device language.
  - A use case that trims the query and does not search below 2 characters.
  - An MVI ViewModel with a 350 ms debounce that cancels pending searches, and an immediate retry.
  - A screen with a focused search field, an idle hint, results with "region, country" subtitles (the region is omitted when it equals the name), and "no cities found" and error states.
  - Strings in English and Spanish.
- `feature:forecast`:
  - Domain model: `Forecast`, `Temperature` and `WeatherCondition` from WMO codes.
  - An offline-first repository on `ForecastSync` and the cache, with a **10-minute freshness rule** for opening a place, while pull to refresh always downloads.
  - A favorite toggle that writes the same favorites table the favorites feature reads.
  - A °C/°F toggle persisted through `core:datastore`.
  - An MVI ViewModel that receives its `ForecastRoute` from navigation.
  - The Material 3 Expressive screen:
    - A hero card with the current temperature on one line that shrinks instead of wrapping, the condition, and today's high and low.
    - Supporting details: feels like, humidity, wind, and the update time.
    - A 7-day list, with "Today" for the first day.
    - Pull to refresh, a full-screen error with retry when nothing is cached, and a snackbar when a refresh fails over cached data.
    - Two panes on windows 840 dp or wider.
  - Strings in English and Spanish.
- `shared`: the placeholders are replaced by the real `searchScreen()` and `forecastScreen()`. Search is removed from the back stack when it opens a forecast. `searchModule` and `forecastModule` join the Koin graph.
- `core:designsystem`: **Google Sans Flex** (the variable font from Google Fonts, SIL OFL 1.1) is bundled as a Compose resource and used by every typography role. The license file ships alongside it.
- Tests: search and forecast use cases, repositories, reducers, ViewModels and screens (JVM and iOS simulator). Navigation of the real `App()` covers search → forecast → back to favorites, and a place saved from its forecast appearing in the favorites list (JVM).
- ADRs 0016–0018, architecture, tech stack and phase docs, and the README roadmap.

Out of scope:
- Deep links, the user's current location, more units (wind speed, 12/24 h) and widgets.
- Konsist architecture tests and the AI usage report (Phase 5).

## Capabilities

### New Capabilities
- `city-search`: finding a place by name. Minimum query length, debounced typing, results in the device language, unambiguous result rows, opening the forecast, and empty and failed searches.
- `weather-forecast`: the forecast of one place. Offline-first loading, the 10-minute reuse rule, the hero and supporting details, the 7-day list, WMO conditions, the temperature unit, saving as a favorite, and the wide-window layout.

### Modified Capabilities
- `app-shell`: screens are now connected without placeholders. A forecast opened from search replaces search in the back stack.
- `favorite-places`: a place saved from its forecast appears in the favorites list without restarting the app.
- `design-system`: the shared theme uses the Google Sans Flex typeface on both platforms.

## Impact

- New modules `feature:search` and `feature:forecast`, registered in `settings.gradle.kts` and `shared`. The placeholder code and strings in `shared` are deleted.
- The new font adds about 3.9 MB to each app (`core/designsystem/src/commonMain/composeResources/font/`), with `OFL.txt` committed next to it.
- New docs: `docs/adr/0016`–`0018` and `docs/phases/phase-4-search-forecast.md`. `docs/architecture.md` drops its Phase 4 markers and placeholder notes. `docs/tech-stack.md` mentions the font.
- The app is now complete for users: search, forecast, favorites, and offline use of cached forecasts.
