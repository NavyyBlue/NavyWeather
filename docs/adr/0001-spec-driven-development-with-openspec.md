# ADR-0001: Spec-driven development with OpenSpec

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 0 — Spec tooling

## Context

The project is a technical challenge where the reasoning matters as much as the code: every phase has to show what was built, why, and how it was verified. Development is also assisted by an AI coding agent, which tends to drift from the intended scope when it works from a loose prompt.

The project needs a way to agree on the expected behavior **before** writing code, keep each step small and reviewable, and end up with an up-to-date description of what the system does.

## Decision

Use **spec-driven development** with [OpenSpec](https://github.com/Fission-AI/OpenSpec) and its `spec-driven` schema. Every phase starts as an OpenSpec change (proposal, spec deltas with `GIVEN / WHEN / THEN` scenarios, design and tasks), is implemented in its own pull request, and is archived into the living specs in `openspec/specs/` once verified. Architectural decisions made along the way are recorded as ADRs in `docs/adr/`.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Code first, documentation at the end | Decisions are reconstructed after the fact, and there is no agreed contract to check the code against. |
| Free-form design docs per phase | No structure for testable requirements, and nothing keeps them current once the phase is done. |
| GitHub Spec Kit | Comparable goal, but organized around one large specification per feature; OpenSpec's change deltas map better to one change per phase and one pull request each. |
| BDD with executable Gherkin (Cucumber) | Adds a test runner and glue code with poor Kotlin Multiplatform support; the scenarios here are verified by regular `kotlin.test` tests instead. |

## Consequences

- Positive: scope is agreed before implementation and each pull request has a clear acceptance checklist (`tasks.md`).
- Positive: the AI agent works from explicit artifacts (`/opsx:propose`, `/opsx:apply`, `/opsx:verify`, `/opsx:archive`), and its output can be checked against them.
- Positive: `openspec/specs/` always reflects the current behavior, phase by phase.
- Negative: more writing per phase. Mitigated by keeping specs focused on observable behavior and by the project rules in `openspec/config.yaml`.
- Negative: spec scenarios are not executed directly. Mitigated by tasks that name the test verifying each requirement, checked again by `/opsx:verify` before archiving.
