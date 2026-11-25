# ADR-0002: Clean Architecture layers as packages, not submodules

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 1 — Build foundation (`phase-1-build-foundation`)

## Context

The challenge asks for feature-based modularization with Clean Architecture and a justified choice between layers as packages or as submodules. In Kotlin Multiplatform every Gradle module compiles for each target (Android, iOS device, iOS simulator and the JVM test host) and its iOS code goes through the Kotlin/Native compiler and linker, the slowest part of the build.

## Decision

Each feature is **one module** with its layers as packages: `domain/`, `data/`, `presentation/` and `di/`. Room entities and DAOs live in `core:database`, because Room needs every entity in a single `@Database`. Layer boundaries are enforced by architecture tests (Konsist) instead of by the module graph.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Per-layer submodules (`:feature:x:domain`, `:data`, `:presentation`) | Roughly triples the number of feature modules, and with it the compilations and Kotlin/Native work, for a boundary that tests can also guarantee. |
| Layer-first modules (`:domain`, `:data`, `:presentation` for the whole app) | Groups unrelated features together, so a change to one feature touches modules shared by all of them; it works against feature isolation. |
| Single module with packages only | Loses feature isolation and the no feature-to-feature rule that the module graph gives for free. |

## Consequences

- Positive: fewer modules, faster builds and simpler Gradle configuration.
- Positive: the feature boundary (a feature never depends on another) is still enforced by Gradle.
- Negative: the compiler does not stop `domain` from importing `data`. Mitigated by architecture tests that fail on such imports (Phase 5).
- Negative: a feature that grows a lot may need to be split later; going from packages to submodules is a mechanical move.
