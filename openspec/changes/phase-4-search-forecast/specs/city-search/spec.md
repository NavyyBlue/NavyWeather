# Spec Delta

## Purpose

Lets the user find a place by name with the Open-Meteo Geocoding API and open its forecast.

## ADDED Requirements

### Requirement: Search starts with the query field ready
The search screen SHALL open with an empty query field that has focus and the software keyboard shown, and a hint SHALL invite the user to type at least two letters. Results SHALL stay reachable above the keyboard.

#### Scenario: User opens search
- **WHEN** the user taps "Search city" on the favorites screen
- **THEN** the search screen opens with a focused, empty query field and the hint "Type at least two letters of a city name."

#### Scenario: Keyboard open while results are listed
- **WHEN** results are listed while the keyboard is visible
- **THEN** the list can scroll so the last result is fully visible above the keyboard

### Requirement: Queries shorter than two characters do not search
The system SHALL trim the query and SHALL NOT call the network when the trimmed query has fewer than 2 characters; the screen SHALL show the idle hint instead.

#### Scenario: One letter typed
- **WHEN** the user types "L"
- **THEN** no request is sent and the idle hint is shown

#### Scenario: Spaces only
- **WHEN** the user types "  L "
- **THEN** no request is sent

#### Scenario: Query cleared
- **WHEN** results for "Lima" are shown and the user clears the query
- **THEN** the results disappear and the idle hint is shown

### Requirement: Typing is debounced so only the latest query is searched
The system SHALL wait 350 ms after the last keystroke before searching, and a new keystroke SHALL cancel any pending search, so results always belong to the latest query. Previous results SHALL stay visible until the new ones arrive.

#### Scenario: Fast typing
- **WHEN** the user types "Li", "Lim" and "Lima" less than 350 ms apart and stops
- **THEN** exactly one request is sent, for "Lima"

#### Scenario: Results stay while refining
- **WHEN** results for "Lim" are shown and the user types "Lima"
- **THEN** the results for "Lim" stay visible until the results for "Lima" arrive

### Requirement: Results are in the device language
The system SHALL request place names in the device language (ISO 639-1 code) and SHALL return at most 10 results per query, without an API key.

#### Scenario: Spanish device
- **WHEN** the user searches "Lima" on a device set to Spanish
- **THEN** the request asks for Spanish names and at most 10 results

### Requirement: Each result identifies the place unambiguously
Each result SHALL show the place name and a subtitle with its region and country. The region SHALL be omitted when it is missing or equals the place name, and the subtitle SHALL be omitted when both are missing.

#### Scenario: Several places share a name
- **WHEN** "Lima" matches places in Peru and in Ohio, United States
- **THEN** the rows read "Lima" with "Lima, Peru" and "Lima" with "Ohio, United States"

#### Scenario: Region equals the name
- **WHEN** a result is "Madrid" in the region "Madrid", Spain
- **THEN** its subtitle reads "Spain"

### Requirement: Selecting a result opens its forecast
Selecting a result SHALL open the forecast of that place with its id, name, country and coordinates, and SHALL remove the search screen from the back stack.

#### Scenario: User picks a place
- **WHEN** the user taps "Lima, Peru" in the results
- **THEN** the forecast screen for Lima opens, and going back returns to the favorites screen

### Requirement: Empty and failed searches are explained
The system SHALL show a "no cities found" state that repeats the query when a search returns nothing, and an error state with the error's message and a retry action when the search fails. Retry SHALL search the same query again immediately, without the debounce delay.

#### Scenario: Nothing matches
- **WHEN** a search for "Xyzzy" returns no results
- **THEN** the screen says no cities were found for "Xyzzy" and suggests checking the spelling

#### Scenario: Network failure and retry
- **WHEN** a search fails because the device is offline and the user taps retry
- **THEN** the connection error is explained, and the same query is searched again immediately
