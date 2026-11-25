# Spec Delta

## Purpose

Keeps the user's saved places on the home screen with their current temperature, as the entry point to search and forecasts.

## ADDED Requirements

### Requirement: Favorites are the home screen
The favorites screen SHALL show a loading state until the saved places are read. When no places are saved it SHALL show an empty state that invites the user to search for a city. When places are saved it SHALL list them with a "Search city" floating action button.

#### Scenario: First launch
- **WHEN** the app opens with no favorites saved
- **THEN** the empty state "No favorite places yet" is shown with a "Search city" button and no floating action button

#### Scenario: Places saved
- **WHEN** the app opens with at least one favorite saved
- **THEN** the list of favorites is shown with a "Search city" floating action button

#### Scenario: Search from favorites
- **WHEN** the user taps "Search city"
- **THEN** the search destination opens

### Requirement: Favorites are listed alphabetically
The list SHALL be sorted by place name and then by country, ignoring letter case and accents.

#### Scenario: Accented names
- **WHEN** the favorites are "Zaragoza", "Ávila", "Ñuñoa", "Nazca" and "Oruro"
- **THEN** the list order is Ávila, Nazca, Ñuñoa, Oruro, Zaragoza

#### Scenario: Same name in different countries
- **WHEN** the favorites are "Santiago" in Spain and "Santiago" in Chile
- **THEN** Santiago, Chile is listed before Santiago, Spain

### Requirement: Each favorite shows its current temperature
Each row SHALL show the place name, its country when known, and its current temperature as a whole number in the user's unit. When no temperature has been downloaded yet the row SHALL show a dash, announced to screen readers as "Temperature not available yet". A change of unit SHALL update every row.

#### Scenario: Temperature known
- **WHEN** Lima's cached temperature is 22 °C and the unit is Fahrenheit
- **THEN** Lima's row shows "72°"

#### Scenario: Temperature not downloaded yet
- **WHEN** a favorite has no cached weather
- **THEN** its row shows "–" and a screen reader announces "Temperature not available yet"

### Requirement: Stale temperatures are refreshed
When the list is first shown, the system SHALL download the current weather, in parallel, only for favorites whose temperature is missing or older than 10 minutes. Pulling to refresh SHALL download all of them. Background refresh failures SHALL be silent; a failed pull to refresh SHALL show a snackbar with the error's message. Cached temperatures SHALL stay visible in both cases, and only one refresh SHALL run at a time.

#### Scenario: Opening the list
- **WHEN** the favorites screen opens and Lima was updated 2 minutes ago and Madrid 30 minutes ago
- **THEN** only Madrid is downloaded

#### Scenario: Pull to refresh
- **WHEN** the user pulls to refresh and every temperature is fresh
- **THEN** every favorite is downloaded

#### Scenario: Pull to refresh offline
- **WHEN** the user pulls to refresh while offline
- **THEN** a snackbar explains the connection error and the previous temperatures remain

#### Scenario: Background refresh offline
- **WHEN** the list opens offline with stale temperatures
- **THEN** no error is shown and the cached temperatures remain

### Requirement: Opening a favorite shows its forecast
Tapping a favorite SHALL open the forecast destination for that place.

#### Scenario: Tap a row
- **WHEN** the user taps Lima in the list
- **THEN** the forecast destination opens for Lima with its id, country and coordinates

### Requirement: Favorites can be removed with undo
Swiping a row from end to start SHALL remove the favorite immediately and show a snackbar with an "Undo" action that restores it. The same removal SHALL be available to screen readers as a custom accessibility action. The snackbar SHALL NOT block other interactions while it is visible.

#### Scenario: Remove and undo
- **WHEN** the user swipes Lima's row toward the start and then taps "Undo" on the snackbar that says "Lima removed"
- **THEN** Lima disappears from the list and then comes back

#### Scenario: Screen reader removal
- **WHEN** a screen reader user chooses the "Remove Lima from favorites" action on Lima's row
- **THEN** Lima is removed as if it had been swiped

#### Scenario: Navigate while the snackbar is shown
- **WHEN** a "removed" snackbar is visible and the user taps another favorite
- **THEN** that favorite's forecast opens immediately
