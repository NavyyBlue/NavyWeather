# ADR-0016: Search as you type, debounced in the ViewModel

- **Status:** Accepted
- **Date:** 2026-11-26
- **Phase:** 4 — Search and forecast (`phase-4-search-forecast`)

## Context

City search should show results while the user types, without a search button. Searching on every keystroke would send several requests per word to a free public API, and responses can arrive out of order, so an older query could overwrite newer results. One or two letters match thousands of places.

## Decision

- **Business rules in the use case**: `SearchCitiesUseCase` trims the query and returns an empty result for fewer than 2 characters without calling the network. It also passes the device language.
- **Timing in the ViewModel**: on every `QueryChanged`, `SearchViewModel` cancels the previous search job and starts a new one that waits **350 ms** before searching. Only the latest query can produce results. `RetryClicked` searches immediately. Results stay on screen until the new ones arrive, and `searchedQuery` records which query they belong to.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| A search button or the keyboard's search action only | One extra step, and no feedback while typing. |
| `Flow.debounce` + `flatMapLatest` on a query `StateFlow` | Equivalent behavior, but it splits the logic between a flow pipeline and the MVI intent handling. A cancellable job keeps every input going through `onIntent`, and it is tested with virtual time. |
| Debounce in the UI (`LaunchedEffect` keyed on the text) | Untestable without UI tests, and it puts timing rules in the view. |
| Client-side filtering of a bundled city list | No up-to-date, localized worldwide data; Open-Meteo already provides it. |

## Consequences

- Positive: at most one request per pause in typing, and results never belong to an older query.
- Positive: every rule (minimum length, debounce, retry) is covered by unit tests with virtual time.
- Negative: results appear 350 ms after the user stops typing, which is a deliberate trade-off.
