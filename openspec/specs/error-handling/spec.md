# error-handling Specification

## Purpose
Defines how operations report success or failure as typed values with a closed set of error kinds, so no layer has to catch framework exceptions and every error can be explained to the user.

## Requirements

### Requirement: Operations return a typed result
Operations that can fail SHALL return either a success carrying its data or a failure carrying an error, instead of throwing, so callers handle both cases explicitly.

#### Scenario: Successful operation
- **WHEN** an operation completes
- **THEN** the caller receives a success with the data

#### Scenario: Failed operation
- **WHEN** an operation fails
- **THEN** the caller receives a failure with an error, and no exception reaches it

### Requirement: Errors belong to a closed set of kinds
Every failure SHALL be one of: network (no connectivity or timeout), server (a non-successful response, keeping its status code), data (a response that cannot be read) or unknown (keeping a diagnostic message when available).

#### Scenario: Server error keeps its status
- **WHEN** a failure is caused by a response with status 503
- **THEN** the error is of kind server and carries the code 503

### Requirement: Results can be transformed without unwrapping
A success SHALL be transformable into another success with a mapping function, and applying that transformation to a failure SHALL return the same failure unchanged.

#### Scenario: Mapping a success
- **WHEN** a success with value 2 is mapped by multiplying by 10
- **THEN** the result is a success with value 20

#### Scenario: Mapping a failure
- **WHEN** a failure with a network error is mapped
- **THEN** the result is the same failure, and the mapping function is not applied
