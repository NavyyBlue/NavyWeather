# presentation-architecture Specification

## Purpose
Defines the MVI contract every screen follows, so state changes are predictable, traceable and testable the same way across all features.

## Requirements

### Requirement: Each screen exposes a single immutable state
A screen's presentation logic SHALL expose exactly one observable state value that describes everything the screen renders, and it SHALL replace that value instead of mutating it.

#### Scenario: Observing the screen
- **WHEN** the UI starts observing a screen's presentation logic
- **THEN** it immediately receives the current state, and later receives each new state as a new value

### Requirement: Intents are the only input
Every user action and every result of background work SHALL reach the presentation logic as an intent through a single entry point.

#### Scenario: Background result
- **WHEN** a background operation started by the screen completes
- **THEN** its result is delivered as an intent through the same entry point as user actions, so it is processed like any other input

### Requirement: State transitions are computed by a pure reducer
The next state SHALL be computed only from the current state and the intent, by a function without side effects, so the same inputs always produce the same state.

#### Scenario: Deterministic transition
- **WHEN** the reducer receives the same state and intent twice
- **THEN** it returns equal states both times, without performing I/O or starting work

### Requirement: Side effects run after the state update
When an intent is received the state SHALL be updated first, and only then SHALL side effects for that intent run, so side effects always observe the updated state.

#### Scenario: Reading state in a side effect
- **WHEN** an intent changes the state and also triggers a side effect
- **THEN** the side effect sees the state already changed by that intent

### Requirement: One-shot events are delivered once
Events that are not part of the state, such as navigation or messages, SHALL be delivered as effects exactly once, in the order they were sent, and SHALL NOT be replayed to a new observer.

#### Scenario: Effect sent while the screen is in the background
- **WHEN** an effect is sent while no UI is observing
- **THEN** it is delivered once when the UI starts observing again

#### Scenario: Configuration change
- **WHEN** an effect has already been delivered and the UI is recreated
- **THEN** the effect is not delivered again

#### Scenario: Effects only while visible
- **WHEN** the screen is not at least started
- **THEN** the UI does not collect effects until it becomes visible again
