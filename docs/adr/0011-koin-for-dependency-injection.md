# ADR-0011: Koin for dependency injection

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 2 — Core infrastructure (`phase-2-core-infrastructure`)

## Context

HTTP clients, the database, DAOs, preferences, repositories, use cases and ViewModels need to be created and scoped once, from common code, on Android and iOS. Each module should declare its own bindings, and the composition root should assemble them.

## Decision

Use **Koin 4**. Each core module and feature exposes a Koin `module`, platform-specific bindings (database and DataStore file paths) come from `expect`/`actual` modules, and the `shared` composition root starts Koin with all of them. Koin's weak spot, graph errors at runtime, is covered by tests: a module test in each core module, and a test of the whole graph in `shared` (Phase 3).

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| kotlin-inject or Metro | The graph is checked at compile time, but they add code generation, which costs extra build time in KMP across four targets. |
| Kodein | Multiplatform, but with a smaller community and fewer Compose integrations. |
| Manual DI (constructor wiring in the composition root) | Works for a small app, but has no ViewModel scoping for Compose, and wiring grows with every feature. |

## Consequences

- Positive: no code generation, a small DSL, and direct ViewModel support in Compose (`koinViewModel()`).
- Negative: a missing binding fails at runtime instead of at build time. Mitigated by tests that resolve the graph.
