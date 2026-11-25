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

### Requirement: Remote failures are classified
Every remote call SHALL produce data or one of the closed error kinds: network when there is no connection or the request times out, server for a non-successful status (keeping the code), data when the response cannot be read, and unknown for anything else. Cancelling the work that made the call SHALL stop it without reporting any error.

#### Scenario: Server unavailable
- **WHEN** a remote service answers with status 503
- **THEN** the result is a server error with code 503

#### Scenario: No connection
- **WHEN** the request cannot reach the service
- **THEN** the result is a network error

#### Scenario: Unexpected response body
- **WHEN** the response is missing required fields or is not valid JSON
- **THEN** the result is a data error

#### Scenario: Call cancelled
- **WHEN** the screen that started a call goes away and its work is cancelled
- **THEN** the call stops and no error is reported

### Requirement: Each error kind has a localized message with a next step
Each error kind SHALL have a user-facing message, in English and in Spanish, that says what happened and what to do next. The language follows the device.

#### Scenario: Offline in English
- **WHEN** a network error is shown to an English-speaking user
- **THEN** the message reads "Check your connection and try again."

#### Scenario: Offline in Spanish
- **WHEN** a network error is shown to a Spanish-speaking user
- **THEN** the message reads "Revisa tu conexión e inténtalo de nuevo."

#### Scenario: Every kind has a message
- **WHEN** a message is requested for each error kind
- **THEN** every kind returns a non-empty message in both languages, and server errors share one message regardless of the code
