# Spec Delta

## Purpose

Defines how user settings are persisted and observed, so every screen applies them consistently across app launches.

## ADDED Requirements

### Requirement: Temperature unit preference
The app SHALL store the temperature unit, Celsius or Fahrenheit, defaulting to Celsius, and SHALL keep it across app launches. Observers SHALL be notified only when the stored unit actually changes.

#### Scenario: Default
- **WHEN** the preference is read on a fresh install
- **THEN** it is Celsius

#### Scenario: Persisted choice
- **WHEN** Fahrenheit is selected and the preferences are read again after the app restarts
- **THEN** the unit is Fahrenheit

#### Scenario: Unchanged value
- **WHEN** the unit is Fahrenheit and Fahrenheit is selected again
- **THEN** observers of the preference are not notified of a change

#### Scenario: Unreadable stored value
- **WHEN** the stored unit is not a known value
- **THEN** the preference is read as Celsius

### Requirement: Temperatures are displayed as whole numbers in the chosen unit
Temperatures, stored in Celsius, SHALL be converted to the chosen unit and rounded to the nearest whole degree for display.

#### Scenario: Celsius
- **WHEN** 21.6 °C is displayed in Celsius
- **THEN** it reads 22

#### Scenario: Fahrenheit
- **WHEN** 20 °C is displayed in Fahrenheit
- **THEN** it reads 68
