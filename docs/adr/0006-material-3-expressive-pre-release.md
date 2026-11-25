# ADR-0006: Material 3 Expressive pre-release

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 1 — Build foundation (`phase-1-build-foundation`)

## Context

The challenge asks for Material 3. Material 3 Expressive (spring motion, `LoadingIndicator`, flexible app bars, button groups, the extended shape scale) only ships in pre-release builds of Compose Multiplatform Material 3, and the latest stable Material 3 predates it and is older than the rest of the Compose 1.12 stack.

## Decision

Use `material3 1.12.0-alpha03`, the line aligned with Compose Multiplatform 1.12, pinned to that exact version in the catalog, with MaterialKolor 5.0.1 built against it. Expressive APIs are used only behind `core:designsystem`, and every experimental API carries an explicit `@OptIn`. It requires `compileSdk 37`; `targetSdk` stays at 36.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Latest stable Material 3 | No Expressive components or motion, and older than the rest of the Compose stack. |
| Custom components imitating Expressive | Reimplements and maintains what the official library already provides. |

## Consequences

- Positive: the current Material design language, with components the stable line does not have.
- Negative: alpha APIs can change. Mitigated by the exact pin and by keeping usage behind the design system, so a breaking change is fixed in one place.
- Negative: `compileSdk 37` is required. When Expressive becomes stable, moving to it is a version change in the catalog.
