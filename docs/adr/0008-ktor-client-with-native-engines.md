# ADR-0008: Ktor Client with native engines and kotlinx.serialization

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 2 — Core infrastructure (`phase-2-core-infrastructure`)

## Context

The app calls two REST APIs from Open-Meteo, the forecast and the geocoding search. The HTTP code should be written once in `commonMain`, respect each platform's network settings, map failures into `AppError`, and be testable without a network.

## Decision

Use **Ktor Client 3** with the **OkHttp** engine on Android and the **Darwin** engine (`NSURLSession`) on iOS, **kotlinx.serialization** for JSON (tolerant to unknown fields), and `HttpTimeout`. One client is built by `HttpClientFactory` and shared through Koin. Its engine is a parameter, so tests use Ktor's `MockEngine`. A `safeApiCall` wrapper turns exceptions into `AppError` and rethrows cancellation.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| Retrofit + OkHttp | Android only; iOS would need a second client behind an interface. |
| Ktorfit | Retrofit-style annotations on top of Ktor, but it adds KSP and a dependency for two endpoints. |
| Apollo Kotlin | GraphQL only; Open-Meteo is REST. |
| Gson or Moshi for JSON | Rely on JVM reflection or code generation and do not run on Kotlin/Native. |

## Consequences

- Positive: one HTTP code path for both platforms, and each one uses its native networking stack.
- Positive: every failure mode is tested with `MockEngine`, including timeouts and cancellation.
- Negative: `core:network` exposes Ktor's `HttpClient` to the modules that build API clients on it (`core:weather` and, later, search). Domain layers never see it.
