# ADR-0009: Room KMP with bundled SQLite for the local cache

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 2 — Core infrastructure (`phase-2-core-infrastructure`)

## Context

The app stores favorite places and a forecast cache that screens observe reactively. The data is relational: the favorites list joins each place with its cached temperature. It has to behave the same on Android and iOS, and the queries should be checked at build time.

## Decision

Use **Room KMP** with the **bundled SQLite driver** and KSP for every target. A single `AppDatabase` in `core:database` holds every entity and DAO. DAOs return `Flow`, writes that touch several tables run in `@Transaction`, and the schema is exported to `core/database/schemas/` and versioned.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| SQLDelight | Mature in KMP and SQL-first with strong typing. Room was preferred because it is maintained by Google as part of Jetpack, Android developers already know it, and it fits the rest of the androidx stack (lifecycle, DataStore). |
| Realm Kotlin | Deprecated. |
| System SQLite instead of the bundled driver | The SQLite version differs between Android and iOS releases; bundling gives the same engine everywhere. |
| Key-value or file cache | No joins, no reactive queries, and no atomic multi-table writes. |

## Consequences

- Positive: compile-time checked queries, reactive reads, and atomic forecast replacement.
- Positive: in-memory databases make DAO tests fast on the JVM and the iOS simulator.
- Negative: KSP runs for four targets in `core:database`, which slows that module's build. Only `core:database` applies the Room convention.
- Negative: Room needs every entity in one database, so entities live in core rather than in features (see ADR-0002).
- Trade-off: favorites and the forecast cache share one database file. On iOS it lives in Application Support, which is included in device and iCloud backups. That is deliberate, because favorites are user data worth restoring, and the forecast rows are small. The re-downloadable rows are not split into a separate file excluded from backup.
