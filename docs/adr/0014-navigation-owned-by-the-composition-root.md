# ADR-0014: Navigation owned by the composition root

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 3 — Favorites and app shell (`phase-3-favorites-app-shell`)

## Context

The app has three screens (favorites, search, forecast) in three feature modules that must not depend on each other (ADR-0002). Favorites opens the forecast and search; search opens the forecast. Routes carry arguments (the forecast needs a place's id, name, country and coordinates), and argument mistakes should fail at compile time. Search and forecast arrive one phase after favorites.

## Decision

Use **Navigation Compose** (multiplatform) with **type-safe `@Serializable` routes** defined in `core:navigation`, whose arguments are primitives only.

- Each feature exposes **one entry point**, a `NavGraphBuilder` extension (for example `favoritesScreen(onNavigateToForecast, onNavigateToSearch)`). It reports navigation through callbacks and never touches a `NavController`.
- Inside a feature, navigation is an MVI effect: the ViewModel emits `NavigateToForecast(route)` and the destination invokes the callback.
- **Only `shared` builds the `NavHost`** and connects the callbacks to routes, including back-stack rules such as replacing search with the forecast it opens.
- Until a feature exists, `shared` registers a **temporary placeholder destination** for its route. The placeholder says the screen is coming and offers a way back, so navigation is complete and testable in every phase. The real feature replaces it.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| String routes (`"forecast/{id}"`) | Arguments are parsed by hand and mistakes show up at runtime. |
| Voyager or Decompose | Each brings its own lifecycle and component model; the official library integrates directly with the androidx `ViewModel` and Koin. |
| Navigation 3 | Promising, but its multiplatform support is recent compared with Navigation Compose. |
| Features navigating to each other directly | Requires feature-to-feature dependencies. |
| Hiding entry points until the target screen exists | Navigation and back behavior could only be tested once every screen exists. |

## Consequences

- Positive: features stay independent and testable alone; a destination's arguments are checked by the compiler.
- Positive: the back stack is defined in one place and verified by a navigation test of the real `App()`.
- Negative: placeholders ship in intermediate builds. They are clearly labeled, have a working back action, and are deleted when the feature lands.
