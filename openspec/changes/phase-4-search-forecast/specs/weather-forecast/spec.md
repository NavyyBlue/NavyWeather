# Spec Delta

## Purpose

Shows the current weather and the next days for one place, offline-first, in the user's unit, and lets the user save the place as a favorite.

## ADDED Requirements

### Requirement: Forecast is offline-first
The forecast screen SHALL show the cached forecast for the place immediately, SHALL refresh it in the background, and SHALL keep showing the cached forecast if the refresh fails, explaining the failure in a snackbar. With nothing cached, it SHALL show a loading state and, if the download fails, a full-screen error with a retry action.

#### Scenario: Cached forecast exists
- **WHEN** the user opens a place whose forecast was downloaded before
- **THEN** the cached forecast is shown without waiting for the network

#### Scenario: Refresh fails with cached data
- **WHEN** a refresh fails while a cached forecast is on screen
- **THEN** the cached forecast stays visible and a snackbar explains the error

#### Scenario: First visit without connection
- **WHEN** the user opens a place with no cached forecast while offline and then taps retry
- **THEN** a full-screen error with a retry button is shown, and retry shows the loading state and tries again

### Requirement: Recent data is reused instead of downloaded again
The system SHALL NOT download the forecast when the cached forecast is younger than 10 minutes, unless the user explicitly refreshes.

#### Scenario: Reopening a place
- **WHEN** the user opens Lima 5 minutes after its forecast was downloaded
- **THEN** no request is sent

#### Scenario: Stale cache
- **WHEN** the user opens Lima 11 minutes after its forecast was downloaded
- **THEN** the forecast is downloaded again

#### Scenario: Pull to refresh
- **WHEN** the user pulls to refresh a fresh forecast
- **THEN** the forecast is downloaded regardless of its age

### Requirement: Current conditions are the hero of the screen
The screen SHALL present the current temperature as its most prominent element, inside the brand-colored hero card, together with the unit, the weather condition and today's high and low. The temperature SHALL stay on one line, shrinking instead of wrapping, and the card SHALL be announced to screen readers as one element.

#### Scenario: Current weather shown
- **WHEN** Lima is 21.6 °C, partly cloudy, with today's range 16–24 °C, shown in Celsius
- **THEN** the hero shows "22" with the unit "°C", the label "Partly cloudy" and "High 24° · Low 16°"

#### Scenario: Screen reader
- **WHEN** a screen reader focuses the hero card
- **THEN** it is announced as one element, for example "22°C, Partly cloudy"

### Requirement: Supporting details and the next days are shown
The screen SHALL show the feels-like temperature, relative humidity and wind speed in km/h, the time the data was downloaded, and one row per forecast day (up to 7) with the day name ("Today" for the current local date), the condition, and the low and high temperatures.

#### Scenario: Seven-day list
- **WHEN** the forecast has 7 days starting today
- **THEN** 7 rows are listed, the first labeled "Today", each with its low and high

### Requirement: Weather conditions come from WMO codes
The system SHALL map Open-Meteo WMO weather codes to conditions: 0 clear, 1 mainly clear, 2 partly cloudy, 3 overcast, 45/48 fog, 51–57 drizzle, 61–67 rain, 71–77 and 85–86 snow, 80–82 showers, 95–99 thunderstorm, and any other code to "unknown conditions", each with a localized label.

#### Scenario: Unknown code
- **WHEN** the API returns weather code 42
- **THEN** the condition reads "Unknown conditions"

### Requirement: Temperatures follow the user's unit
All temperatures on the screen SHALL be shown in the unit chosen with the °C/°F toggle, rounded to whole degrees. Changing the unit SHALL update every temperature at once and persist the choice for the whole app.

#### Scenario: Switch to Fahrenheit
- **WHEN** the forecast shows 22 °C and the user selects °F
- **THEN** the hero shows 72 °F, every other temperature switches too, and the favorites list also shows °F

### Requirement: The place can be saved as a favorite from the forecast
The app bar SHALL show a favorite toggle reflecting whether the place is saved, with an accessible label. Toggling it SHALL save or remove the place immediately.

#### Scenario: Save a favorite
- **WHEN** the user taps the favorite toggle on a place that is not saved
- **THEN** the toggle shows the place as saved at once and the place is stored as a favorite

#### Scenario: Remove a favorite
- **WHEN** the user taps the favorite toggle on a saved place
- **THEN** the toggle shows the place as not saved and it is removed from favorites

### Requirement: Wide windows show more, not bigger
On windows at least 840 dp wide the screen SHALL show the current conditions and the day list side by side; narrower windows SHALL show a single scrolling column.

#### Scenario: Tablet in landscape
- **WHEN** the forecast is shown in a window 1000 dp wide
- **THEN** the current conditions are on the left and the day list is on the right
