# ADR-0018: Google Sans Flex as the app typeface

- **Status:** Accepted
- **Date:** 2026-11-26
- **Phase:** 4 — Search and forecast (`phase-4-search-forecast`)

## Context

With the default typography, Android renders Roboto and iOS renders SF Pro, so the shared UI looks different on each platform, and the forecast's large temperature numeral does not carry the brand. Material 3 Expressive is designed around Google Sans Flex, which is published on Google Fonts.

## Decision

Bundle **Google Sans Flex**, the single variable TTF from the `google/fonts` repository (`ofl/googlesansflex`), as a Compose resource in `core:designsystem`. Build every typography role from it, including the Expressive emphasized roles, with explicit instances for weights 400, 500, 600 and 700 on the variable `wght` axis. The license (`OFL.txt`) and the trademark note (`TRADEMARKS.md`) ship inside the app under `files/licenses/google-sans-flex`.

**License:** SIL Open Font License 1.1, with no Reserved Font Names. The font is used **unmodified**. "Google Sans Flex" is a Google trademark, and the app does not use it as part of its own name and does not imply affiliation with Google.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Platform system fonts | Different look per platform, and no brand character in the hero numeral. |
| Downloadable fonts (Google Play services) | Android only, and it needs network access on first launch. |
| Static font files per weight | Four or more files; one variable file covers every weight. |
| Another open font (Inter, Roboto Flex) | Valid options, but Google Sans Flex is the typeface Material 3 Expressive is designed with. |

## Consequences

- Positive: the same type on Android and iOS, and a stronger hero numeral; tabular figures still work (`tnum`).
- Positive: a test asserts that every role uses the family, so a regression to the system font is caught.
- Negative: about 3.9 MB more per app. Subsetting to Latin characters can reduce it later without code changes.
