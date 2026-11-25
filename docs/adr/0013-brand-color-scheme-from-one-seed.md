# ADR-0013: Color scheme generated from one brand seed

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 2 — Core infrastructure (`phase-2-core-infrastructure`)

## Context

The app needs a complete Material 3 color scheme (about 50 roles, light and dark) that reads as the brand navy, looks the same on Android and iOS, and keeps the contrast pairs Material guarantees. Material 3 Expressive recommends dynamic color, which only exists on Android 12+.

## Decision

Generate every role at runtime from one seed, **#1F3A93**, with **MaterialKolor** and the **Fidelity** palette style, which keeps the seed itself as the light `primaryContainer`. The brand scheme is the default on both platforms. Wallpaper-based dynamic color is an opt-in parameter, used only on Android 12+. Usage rules (`primaryContainer` only for a screen's hero, tertiary only as a status accent) are part of the design-system spec.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Material Theme Builder export | Produces static hex values; a brand change means regenerating and replacing every role. |
| Hand-picked colors | Easy to break contrast pairs and the light/dark relationships. |
| Dynamic color by default | Not available on iOS, so the two platforms would look different, and the brand would disappear on Android. |
| TonalSpot (Material's default style) | Desaturates the seed, so the app no longer reads as navy. Fidelity keeps the seed's chroma. |

## Consequences

- Positive: a brand change is one line, and every role keeps Material's contrast guarantees.
- Positive: a test asserts that the light `primaryContainer` equals the seed, which catches changes in the generator.
- Negative: one more dependency on the UI side. Its version is pinned with the Material 3 pre-release (ADR-0006).
