# ADR-0012: Offline-first with the local database as the single source of truth

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 2 — Core infrastructure (`phase-2-core-infrastructure`)

## Context

Weather is checked often, sometimes without a connection, and the same data appears on two screens: the favorites list shows each place's temperature, and the forecast screen shows the full forecast. Both features need to download a forecast, but a feature must not depend on another feature (ADR-0002).

## Decision

The **local database is the single source of truth**. Screens observe it; the network only writes into it. Downloading a forecast into the cache is done by `ForecastSync` in **`core:weather`**, which both features use. `ForecastSync` records when each place was downloaded. The rules for **when** to download (the 10-minute freshness window, pull to refresh) belong to each feature's domain layer.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Network first, cache as a fallback | The screen waits for the network every time, and two screens can show different values for the same place. |
| In-memory cache only | Lost when the process dies, so there is nothing to show offline after a restart. |
| Each feature downloads its own forecast | Duplicates the API client and the mapping, and the two features would write the same tables in different ways. |
| Forecast download inside `feature:forecast`, used by favorites | Creates a feature-to-feature dependency. |

## Consequences

- Positive: screens render cached data immediately, and stay consistent with each other because they read the same rows.
- Positive: a failed download never removes cached data.
- Negative: the cache can be stale. Each feature decides when to refresh and shows the download time when it matters.
