# Spec Delta

## REMOVED Requirements

### Requirement: Screens are connected through typed routes
**Reason**: Its placeholder clause and the "Screen not available yet" scenario only existed while search and forecast were missing. Every screen now exists, so the requirement is replaced by "Navigation follows typed routes and the screen history", which keeps the route contract and adds the search back-stack rule.
**Migration**: The placeholder destinations in the composition root are deleted; the real search and forecast screens use the same routes.

## ADDED Requirements

### Requirement: Navigation follows typed routes and the screen history
The app SHALL navigate between favorites, search and forecast through typed routes, and the forecast route SHALL carry the place id, name, optional country and coordinates. Back navigation SHALL follow the screen history, except that search is replaced by the forecast it opens, so going back from that forecast returns to favorites.

#### Scenario: Back from a forecast opened from favorites
- **WHEN** the user opens a place's forecast from the favorites list and goes back
- **THEN** the favorites screen is shown

#### Scenario: Forecast route arguments
- **WHEN** a forecast is opened for Lima, Peru at -12.05, -77.04 with id 3936456
- **THEN** the forecast destination receives exactly that id, name, country and coordinates

#### Scenario: Back from a forecast opened from search
- **WHEN** the user goes favorites → search → forecast and then goes back
- **THEN** the favorites screen is shown

#### Scenario: Back from search
- **WHEN** the user opens search from favorites and goes back without choosing a place
- **THEN** the favorites screen is shown
