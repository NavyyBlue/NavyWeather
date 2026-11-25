# weather-data Specification

## Purpose
Defines how the forecast of a place is obtained from Open-Meteo and written into the local cache, which is the only place screens read it from.

## Requirements

### Requirement: A forecast is downloaded for a place's coordinates
Downloading a forecast SHALL request, for the place's latitude and longitude, its current conditions (temperature, apparent temperature, relative humidity, weather code, wind speed) and 7 days of daily weather code and maximum and minimum temperatures, in metric units and in the place's own time zone. No API key SHALL be required.

#### Scenario: Request contents
- **WHEN** a forecast is downloaded for latitude -12.05 and longitude -77.04
- **THEN** the request asks Open-Meteo for those coordinates, the current and daily fields above, 7 forecast days and the location's time zone

### Requirement: A downloaded forecast is written into the cache
A successful download SHALL replace the place's cached forecast and SHALL record the time of the download, so later readers know how old the cached data is.

#### Scenario: Successful download
- **WHEN** a forecast is downloaded for a place at a known time
- **THEN** the cache holds the new current conditions and days for that place, and its download time equals that time

#### Scenario: Last download time
- **WHEN** the last download time of a place is requested
- **THEN** it returns the time of the latest successful download, or nothing if the place was never downloaded

### Requirement: A failed download keeps the cache intact
When a download fails, the cache SHALL keep the forecast it had, and the failure SHALL be reported as a typed error.

#### Scenario: Server error
- **WHEN** a place has a cached forecast and a new download fails with a server error
- **THEN** the cached forecast is unchanged and the caller receives the server error with its status code

### Requirement: Malformed daily data keeps the valid days
When the daily lists in a response have different lengths, only the days present in every list SHALL be stored.

#### Scenario: One list is shorter
- **WHEN** a response has 7 dates and 7 weather codes but only 6 maximum and minimum temperatures
- **THEN** the first 6 days are stored
