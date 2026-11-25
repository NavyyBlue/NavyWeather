# Proposal

## Why

Phase 1 left a build and the base contracts, but nothing that talks to Open-Meteo, stores data on the device, remembers a setting or draws a screen. The three features (favorites, search, forecast) all need the same infrastructure, so it is built once, in core modules, before any feature exists. That way Phases 3 and 4 only add feature logic on top of tested pieces, and no feature has to depend on another to reuse them.

## What Changes

- `core:network`: one HTTP client for the app on the native engine of each platform (OkHttp on Android, Darwin on iOS). It reads JSON while ignoring fields it does not know, has connect and request timeouts, and logs requests only when the composition root enables it. A safe call wrapper turns every failure into an `AppError` and never swallows coroutine cancellation.
- `core:database`: a Room database with bundled SQLite holding favorite places, the current weather and the daily forecast of each place. It provides reactive queries (including favorites joined with their cached temperature), replaces a place's forecast in one transaction, and exports its schema. It is stored in the app's private storage on Android and in Application Support on iOS.
- `core:datastore`: user preferences backed by DataStore. The first one is the temperature unit: Celsius by default, persisted, emitted only when it changes, and able to convert a Celsius value to the display unit.
- `core:weather`: the Open-Meteo Forecast API client (current conditions plus 7 days, in the place's own time zone) and `ForecastSync`. `ForecastSync` downloads a place's forecast into the cache and reports when the place was last synced. It lives in core because both favorites and forecast need it.
- `core:designsystem`:
  - The Material 3 Expressive theme, with the color scheme generated from the brand seed `#1F3A93` (Fidelity style) and wallpaper color as an opt-in on Android 12+, plus expressive motion, the corner scale, spacing and typography with tabular numbers.
  - Full-screen loading, empty and error states.
  - A localized message, in English and Spanish, for each kind of error.
- A Koin module for each core module that needs one.
- Tests for each module on the JVM and the iOS simulator. That includes the weather client and sync, and the design system's theme and error messages.
- ADRs 0008–0013 for the decisions made in this phase. Updated docs: architecture, tech stack, a doc for this phase and the README roadmap.

Out of scope:
- The **10-minute freshness rule**. `ForecastSync` exposes the last sync time, but the decision to skip a download belongs to each feature's domain layer, so it is specified with the features.
- Features, the `shared` composition root and the Android/iOS apps, including wiring HTTP logging to debug builds (Phases 3–4).
- Konsist architecture tests and the AI usage report (Phase 5).

## Capabilities

### New Capabilities
- `networking`: how the app talks to remote services. Native engines, tolerant JSON, timeouts, and request logging that stays off unless enabled.
- `local-cache`: what the app keeps on the device and how it is observed. Favorites, the current weather and daily forecast per place, atomic replacement, data that survives restarts, and where it is stored on each platform.
- `weather-data`: downloading a place's forecast from Open-Meteo into the local cache, and what happens to the cache when a download fails.
- `user-preferences`: persisted user settings. The first one is the temperature unit and its conversion.
- `design-system`: the shared theme (brand color generation, dynamic color opt-in, shapes, motion, type) and the shared full-screen states.

### Modified Capabilities
- `error-handling`: adds requirements for remote failures (how network, server, data, unknown and cancellation cases are classified) and for the localized message attached to each kind of error.

## Impact

- New modules `core:network`, `core:database`, `core:datastore`, `core:weather` and `core:designsystem`, registered in `settings.gradle.kts`.
- New runtime dependencies already pinned in the catalog are now used: Ktor (core, content negotiation, logging, OkHttp, Darwin, mock for tests), Room and bundled SQLite with KSP, DataStore and Okio, Koin, Kermit, MaterialKolor, and Compose UI testing for the design system.
- `core:database` checks its exported schema (`schemas/.../1.json`) into the repository.
- New docs: `docs/adr/0008`–`0013` and `docs/phases/phase-2-core-infrastructure.md`. `docs/architecture.md` drops its "(Phase 2)" markers.
- No user-facing app yet. The behavior specified here is observed through tests until the app shell arrives in Phase 3.
