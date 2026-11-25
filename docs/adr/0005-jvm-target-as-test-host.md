# ADR-0005: JVM target as the shared test host

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 1 — Build foundation (`phase-1-build-foundation`)

## Context

Most logic and UI lives in `commonMain`, and its tests should run fast and often. Android unit tests that touch Room, Compose or Android APIs need Robolectric or an emulator, and Robolectric annotations cannot live in common code. iOS tests run on the simulator and are slower.

## Decision

Add a `jvm()` target to every shared module **only as a test host**. It is never shipped. Shared tests run on the JVM (fast, including in-memory Room, Compose UI tests and Koin graph verification) and on the iOS simulator, through `./gradlew allTests`.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Android host tests with Robolectric | Needs Android-specific annotations and setup that cannot be written in `commonTest`. |
| Android instrumented tests | Need an emulator or device; too slow for the inner loop and for every module. |
| iOS simulator only | Slower, and some tooling (Compose UI tests, Room in-memory drivers) is easier and faster on the JVM. |

## Consequences

- Positive: the whole common test suite runs in seconds without an emulator.
- Positive: the same tests also run on the iOS simulator, catching Kotlin/Native differences.
- Negative: one extra compilation per module.
- Negative: Android-specific source sets are not unit tested; they are kept as thin platform glue. AGP's warning about disabled Android host tests is suppressed with a comment explaining why.
