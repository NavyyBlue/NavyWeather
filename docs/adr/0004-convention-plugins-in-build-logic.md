# ADR-0004: Convention plugins in an included build-logic build

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 1 — Build foundation (`phase-1-build-foundation`)

## Context

Every shared module needs the same targets, Android namespace, Kotlin settings and test dependencies, and modules of the same role (UI, feature, database) need the same extra setup. Copying that configuration into each build file makes modules drift apart and makes reviews harder.

## Decision

Put shared build configuration in an included `build-logic` build with one **convention plugin per module role**: `navyweather.kmp.library`, `navyweather.kmp.compose`, `navyweather.kmp.feature` and `navyweather.room`. A module applies one plugin and declares only its own dependencies. The Android namespace is derived from the Gradle path.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| `subprojects {}` / `allprojects {}` in the root build | Implicit configuration applied at a distance; discouraged by Gradle and a problem for configuration cache and project isolation. |
| `buildSrc` | Works, but any change invalidates the whole build; an included build is the recommended form. |
| Copy-pasted build files | Drifts over time and multiplies the places to update on every version bump. |

## Consequences

- Positive: adding a module is one plugin line plus its dependencies; a version or target change is made once.
- Positive: unique Android namespaces without manual values.
- Negative: the build logic is Kotlin code that has to be learned and maintained. Kept small: four plugins and one helper file.
