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
| [0002](0002-clean-architecture-layers-as-packages.md) | Clean Architecture layers as packages, not submodules | Accepted | 1 |
| [0003](0003-in-house-mvi-contract.md) | MVI through an in-house contract | Accepted | 1 |
| [0004](0004-convention-plugins-in-build-logic.md) | Convention plugins in an included build-logic build | Accepted | 1 |
| [0005](0005-jvm-target-as-test-host.md) | JVM target as the shared test host | Accepted | 1 |
| [0006](0006-material-3-expressive-pre-release.md) | Material 3 Expressive pre-release | Accepted | 1 |
| [0007](0007-gradle-daemon-on-jdk-25.md) | Gradle daemon pinned to JDK 25 | Accepted | 1 |
| [0008](0008-ktor-client-with-native-engines.md) | Ktor Client with native engines and kotlinx.serialization | Accepted | 2 |
| [0009](0009-room-kmp-with-bundled-sqlite.md) | Room KMP with bundled SQLite for the local cache | Accepted | 2 |
| [0010](0010-datastore-for-preferences.md) | DataStore Preferences for user settings | Accepted | 2 |
| [0011](0011-koin-for-dependency-injection.md) | Koin for dependency injection | Accepted | 2 |
| [0012](0012-offline-first-single-source-of-truth.md) | Offline-first with the local database as the single source of truth | Accepted | 2 |
| [0013](0013-brand-color-scheme-from-one-seed.md) | Color scheme generated from one brand seed | Accepted | 2 |
| [0014](0014-navigation-owned-by-the-composition-root.md) | Navigation owned by the composition root | Accepted | 3 |
| [0015](0015-ios-static-framework-built-from-xcode.md) | iOS static framework built from Xcode | Accepted | 3 |
| [0016](0016-search-as-you-type.md) | Search as you type, debounced in the ViewModel | Accepted | 4 |
| [0017](0017-adaptive-forecast-layout.md) | Forecast layout adapts to the available width | Accepted | 4 |
| [0018](0018-google-sans-flex-typeface.md) | Google Sans Flex as the app typeface | Accepted | 4 |
