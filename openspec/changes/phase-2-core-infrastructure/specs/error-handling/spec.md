# Spec Delta

## ADDED Requirements

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
