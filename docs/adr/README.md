# Architecture Decision Records

An ADR records one architectural or technology decision: the context, what was decided, the alternatives and the consequences. It answers **why** the project is the way it is; the [architecture](../architecture.md) and [tech stack](../tech-stack.md) documents describe **what** it is and link here.

## How ADRs are used

- One file per decision, numbered in order: `NNNN-short-title.md`, based on the [template](template.md).
- An ADR is written in the same OpenSpec change (and pull request) that introduces the decision, and that change's `design.md` links to it.
- Once accepted, an ADR is not rewritten. If the decision changes, a new ADR supersedes it and the old one is marked `Superseded by`.

## ADRs and specs

| | OpenSpec specs | ADRs |
|---|---|---|
| Record | What the system must do (requirements and scenarios) | Why a decision was made (alternatives and trade-offs) |
| Change over time | Updated when a change is archived; always describe the current behavior | Immutable; superseded by new ADRs |
| Location | `openspec/specs/` | `docs/adr/` |

## Index

| ADR | Decision | Status | Phase |
|---|---|---|---|
| [0001](0001-spec-driven-development-with-openspec.md) | Spec-driven development with OpenSpec | Accepted | 0 |
