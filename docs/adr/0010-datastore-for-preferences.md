# ADR-0010: DataStore Preferences for user settings

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 2 — Core infrastructure (`phase-2-core-infrastructure`)

## Context

The app needs to persist a few user settings, starting with the temperature unit. Settings must be readable from `commonMain`, observable (screens update when the unit changes) and safe to write without blocking the UI thread.

## Decision

Use **DataStore Preferences** (multiplatform, on Okio). A single instance per file is held as a Koin `single`, and the file path is provided per platform. Features depend on a `UserPreferencesRepository` interface, never on DataStore itself.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| SharedPreferences / NSUserDefaults directly | Platform-specific, so two implementations behind an interface. SharedPreferences also reads synchronously on first access, its `apply()` can block the main thread when the activity pauses (a known ANR source), and it reports no disk errors. Google recommends migrating to DataStore. |
| multiplatform-settings | A valid, simpler KMP wrapper over SharedPreferences and NSUserDefaults, but its core API is synchronous and Flow support is an add-on. DataStore is asynchronous and Flow-based by design, matching Room and the rest of the stack, and is maintained as part of Jetpack. |
| A Room table | A schema and DAO for a handful of key-value settings is more than they need. |

## Consequences

- Positive: asynchronous, transactional writes and a `Flow` that screens observe directly.
- Positive: tested on both targets with a temporary file per test.
- Negative: DataStore throws if two instances open the same file, which the Koin `single` prevents. Each platform must also provide a file path.
