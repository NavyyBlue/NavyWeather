# local-cache Specification

## Purpose
Defines what the app keeps on the device and how the rest of the app observes it, so screens can show data without the network and react when it changes.

## Requirements

### Requirement: Favorite places are stored on the device
The cache SHALL store each favorite place with its identifier, name, optional country, coordinates and the time it was added. Saving a place that is already a favorite SHALL update it instead of creating a duplicate, and a place SHALL be removable by its identifier.

#### Scenario: Saving the same place twice
- **WHEN** a place is saved as a favorite and then saved again with the same identifier
- **THEN** the cache holds a single entry for it, with the latest values

#### Scenario: Removing a favorite
- **WHEN** a favorite is removed by its identifier
- **THEN** it no longer appears among the favorites

### Requirement: Weather is cached per place
For each place the cache SHALL store its current conditions (temperature, apparent temperature, humidity, wind speed, weather code, observation time and the time it was downloaded) and one daily entry per date (weather code, maximum and minimum temperature). Temperatures SHALL be stored in Celsius.

#### Scenario: Reading a cached forecast
- **WHEN** a forecast for a place has been stored
- **THEN** its current conditions and its days, ordered by date, can be read back for that place

### Requirement: Cached data is observable
Reads used by screens SHALL be observable: an observer SHALL receive the current data immediately and again after every change that affects it. Observers of favorites SHALL be able to receive each favorite together with its cached temperature and download time, which are absent when no weather is cached for it yet.

#### Scenario: Favorite gains a temperature
- **WHEN** a favorite with no cached weather is being observed and a forecast for it is stored
- **THEN** the observer receives the favorite again, now with its temperature

#### Scenario: Favorite status of a place
- **WHEN** a place is observed for being a favorite and it is saved and then removed
- **THEN** the observer receives not favorite, favorite, and not favorite again

### Requirement: A place's forecast is replaced atomically
Storing a new forecast for a place SHALL replace its current conditions and all its days in one step: observers SHALL never see a mix of old and new data, days missing from the new forecast SHALL be removed, and a repeated date in the new forecast SHALL NOT make the replacement fail.

#### Scenario: Forecast window moves forward
- **WHEN** a place has days from Monday to Sunday cached and a new forecast from Tuesday to Monday is stored
- **THEN** the cached days are exactly Tuesday to Monday

#### Scenario: Repeated date in the new forecast
- **WHEN** a new forecast contains the same date twice
- **THEN** it is stored with one entry for that date and the rest of the forecast is kept

### Requirement: The cache survives restarts in private storage
The cache SHALL persist across app restarts. On Android it SHALL live in the app's private storage. On iOS it SHALL live in the app's Application Support directory, which is not visible to the user in the Files app.

#### Scenario: App restarted
- **WHEN** data is stored and the app is closed and opened again
- **THEN** the same data is available without downloading it
