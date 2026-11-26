# ADR-0017: Forecast layout adapts to the available width

- **Status:** Accepted
- **Date:** 2026-11-26
- **Phase:** 4 — Search and forecast (`phase-4-search-forecast`)

## Context

The forecast has two groups of content: current conditions (hero and details) and the next days. On a phone they fit in one scrolling column. On tablets, foldables, landscape phones and desktop-sized windows, one stretched column wastes space and makes line lengths uncomfortable.

## Decision

The forecast screen reads its **available width** with `BoxWithConstraints`. At **840 dp or more** (Material's expanded width class), current conditions and the day list are shown **side by side**, each in its own scrolling column. Below that, one column limited to 640 dp is centered. The decision depends only on the space the screen gets, not on the device type.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| A single layout everywhere | Wastes space on wide windows; Material's guidance is to show more, not bigger. |
| Device type checks (tablet vs phone) | Breaks in split screen, freeform windows and foldables, where the window, not the device, matters. |
| `WindowSizeClass` from the adaptive library | Would add a dependency for one breakpoint in one screen; the local width is enough and also works when the screen is embedded in a smaller pane. |

## Consequences

- Positive: tablets and landscape windows show current conditions and the week at once.
- Positive: both layouts are covered by UI tests that measure the hero and the day list at 1000 dp and 400 dp.
- Negative: two layouts to maintain. They share the same list items, so only the arrangement differs.
