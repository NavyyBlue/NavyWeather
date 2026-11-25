# Spec Delta

## Purpose

Defines how the app talks to remote services: one client per app on each platform's native networking stack, tolerant reading of responses, bounded waiting, and request logs that never leak by default.

## ADDED Requirements

### Requirement: Requests use the platform's native networking
Remote requests SHALL go through the operating system's own networking stack on each platform, so they follow its proxy, VPN and network settings.

#### Scenario: Request on iOS
- **WHEN** the app performs a request on iOS
- **THEN** it goes through the system URL loading stack and respects the device's network settings

### Requirement: Responses tolerate new fields
Reading a response SHALL ignore fields the app does not know and SHALL treat absent optional fields as missing values, so a service adding fields does not break the app.

#### Scenario: Service adds a field
- **WHEN** a response contains the expected fields plus a field the app does not know
- **THEN** the data is read successfully and the unknown field is ignored

### Requirement: Waiting is bounded
A request SHALL fail as a network error if the connection is not established within 10 seconds or the whole request does not complete within 15 seconds.

#### Scenario: Unresponsive server
- **WHEN** a server accepts the connection but does not answer within 15 seconds
- **THEN** the request ends with a network error instead of waiting indefinitely

### Requirement: Request logging is off unless enabled
Request logging SHALL be disabled by default and SHALL be enabled only when the app's composition root asks for it explicitly, which it does only in debug builds. Request URLs and coordinates SHALL NOT reach the system log otherwise.

#### Scenario: Logging not requested
- **WHEN** the network layer is created without enabling logging and a request is performed
- **THEN** nothing about the request is written to the log

#### Scenario: Logging requested
- **WHEN** the network layer is created with logging enabled and a request is performed
- **THEN** the request line and response status are written to the debug log
