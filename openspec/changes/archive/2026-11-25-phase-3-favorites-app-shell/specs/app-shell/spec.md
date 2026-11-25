# Spec Delta

## Purpose

Defines how the shared app is hosted on Android and iOS, how screens connect, and the platform conventions every screen follows.

## ADDED Requirements

### Requirement: One shared UI runs on Android and iOS
The same shared UI, navigation and business logic SHALL run on Android (minimum API 26) and iOS, each hosted by a native entry point: an Android activity and a SwiftUI app. The app SHALL open on the favorites screen.

#### Scenario: Android launch
- **WHEN** the user opens the Android app
- **THEN** the favorites screen of the shared UI is shown

#### Scenario: iOS launch
- **WHEN** the user opens the iOS app
- **THEN** the same favorites screen is shown, rendered by the shared UI

### Requirement: Screens are connected through typed routes
The app SHALL navigate between favorites, search and forecast through typed routes, and the forecast route SHALL carry the place id, name, optional country and coordinates. Back navigation SHALL follow the screen history. A screen that is not available yet SHALL be reachable as a placeholder that explains it is coming, instead of failing.

#### Scenario: Back from a forecast opened from favorites
- **WHEN** the user opens a place's forecast from the favorites list and goes back
- **THEN** the favorites screen is shown

#### Scenario: Forecast route arguments
- **WHEN** a forecast is opened for Lima, Peru at -12.05, -77.04 with id 3936456
- **THEN** the forecast destination receives exactly that id, name, country and coordinates

#### Scenario: Screen not available yet
- **WHEN** the user opens search before the search screen exists
- **THEN** a placeholder screen explains it is not available yet and offers a way back

### Requirement: Content is drawn edge to edge
Screens SHALL draw behind the system bars while keeping text and controls clear of them, in portrait and landscape.

#### Scenario: Landscape with side navigation bar
- **WHEN** a list is shown on an Android phone in landscape with three-button navigation on the side
- **THEN** no row or action sits under the navigation bar

### Requirement: The UI follows the device language
All UI text SHALL be available in English and Spanish and SHALL follow the device language, defaulting to English for any other language.

#### Scenario: Spanish device
- **WHEN** the app opens on a device set to Spanish
- **THEN** the favorites subtitle reads "Tus lugares"

#### Scenario: Unsupported language
- **WHEN** the app opens on a device set to a language other than English or Spanish
- **THEN** the texts are in English

### Requirement: Light and dark themes follow the system
The app SHALL follow the system light or dark setting on both platforms, and the window background shown before the UI draws SHALL match the theme's surface color.

#### Scenario: Dark mode
- **WHEN** the app opens with the system in dark mode
- **THEN** every screen uses the dark color scheme, with no light flash at launch on Android

### Requirement: Request logging only in debug builds
The app SHALL enable request logging in debug builds only. Release builds SHALL NOT write request URLs or coordinates to the system log.

#### Scenario: Release build
- **WHEN** a release build performs requests
- **THEN** nothing about the requests is written to the log
