# ADR-0003: MVI through an in-house contract

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 1 — Build foundation (`phase-1-build-foundation`)

## Context

Every screen needs the same presentation pattern. The challenge accepts MVVM or MVI. The project needs one source of truth per screen, transitions that are easy to test, and a safe way to send one-shot events (navigation, snackbars) that must not repeat when the UI is recreated.

## Decision

Use **MVI**: an immutable `UiState`, a sealed `UiIntent` as the only input, a pure `Reducer` `(State, Intent) -> State` and one-shot `UiEffect`s. It is implemented as a small in-house contract in `core:mvi` on the multiplatform androidx `ViewModel`: `MviViewModel` applies the reducer before running side effects, and effects go through a buffered channel that delivers each one once.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| MVVM | Several loose `StateFlow`s can drift out of sync, transition logic is spread across methods, and there is no standard pattern for one-shot events. |
| Orbit MVI | Multiplatform and mature, but adds its own DSL and dependency for something that fits in one class. |
| MVIKotlin | Very complete (stores, time travel), but designed around Decompose and heavier than this app needs. |

## Consequences

- Positive: impossible state combinations cannot be represented, and reducers are tested as plain functions without coroutines or mocks.
- Positive: works directly with `koinViewModel()` and the androidx lifecycle on both platforms.
- Negative: more boilerplate per screen (contract, reducer, ViewModel). Reduced by the shared base class.
- Negative: the contract is maintained in-house; it is covered by `MviViewModelTest`.
