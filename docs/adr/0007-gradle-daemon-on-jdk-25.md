# ADR-0007: Gradle daemon pinned to JDK 25

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 1 — Build foundation (`phase-1-build-foundation`)

## Context

The build needs a predictable JDK on every machine. Gradle can pin the daemon JVM through `gradle/gradle-daemon-jvm.properties`, optionally with toolchain repositories that download a JDK automatically.

## Decision

Pin the Gradle daemon to **JDK 25** (the current LTS) with `toolchainVersion=25`, and **do not** configure auto-download repositories. Only the build runs on this JDK; the app's bytecode targets are set by AGP and Kotlin.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| JDK 21 | Also an LTS and supported, but older; JDK 25 is the current LTS and the build tools in the catalog support it. |
| No pin (use whatever `JAVA_HOME` is) | Builds behave differently between machines and IDEs. |
| Pin with auto-download (foojay resolver) | Downloads a JDK silently from a third-party resolver; an explicit error and a documented prerequisite were preferred. |

## Consequences

- Positive: the same build JVM everywhere, and a clear error when it is missing.
- Negative: contributors must install JDK 25 themselves; it is listed in the README prerequisites.
