# design-system Specification

## Purpose
Defines the shared visual language every screen uses: the theme, how its colors are produced and used, and the reusable full-screen states.

## Requirements

### Requirement: One shared Material 3 Expressive theme
All screens SHALL use one theme from the shared design system, based on Material 3 Expressive, with its spring-based motion, its extended corner scale (4, 8, 12, 16, 20, 28, 32 and 48 dp), a shared spacing scale, and numbers that keep the same width so changing values do not shift the layout.

#### Scenario: Feature screen styling
- **WHEN** a feature needs a color, type style, shape or spacing value
- **THEN** it takes it from the shared theme instead of defining a local value

### Requirement: Colors are generated from a single brand seed
Every color role, in light and dark, SHALL be generated from the brand seed #1F3A93 with the Fidelity palette style, so the seed itself is the primaryContainer color in light mode. Wallpaper-based dynamic color SHALL be available only as an opt-in on Android 12 and later; everywhere else the brand scheme is used.

#### Scenario: Brand container
- **WHEN** the default theme is shown in light mode
- **THEN** primaryContainer is #1F3A93

#### Scenario: Light and dark
- **WHEN** the theme is shown in dark mode
- **THEN** every role comes from the same seed, and primary differs from the light scheme's primary

#### Scenario: Dynamic color requested on iOS
- **WHEN** dynamic color is requested on a platform that does not support it
- **THEN** the brand scheme is used

### Requirement: Emphasis is spent deliberately
A screen SHALL reserve the strong primaryContainer fill for its hero element. Tertiary colors SHALL be used only as status accents, selections SHALL use secondary roles, and lists SHALL sit on surface roles. Text and icons SHALL use their paired "on" roles, never lowered opacity.

#### Scenario: Shared states
- **WHEN** a shared loading, empty or error state is shown
- **THEN** it sits on surface roles and uses primary only for its main action or indicator

### Requirement: Shared loading, empty and error states
The design system SHALL provide full-screen loading, empty and error states. The error state SHALL show the error's message and a retry action as its primary button. Empty and error states SHALL scroll when their content does not fit, so their action is always reachable. Each state SHALL be announced to screen readers with its text.

#### Scenario: Error state retry
- **WHEN** an error state is shown and the user taps retry
- **THEN** the retry action is invoked once

#### Scenario: Error at large font size in a short window
- **WHEN** an error state is shown with a 200% font size in a window too short for its content
- **THEN** the user can scroll to the retry button

#### Scenario: Loading state
- **WHEN** a loading state is shown
- **THEN** a screen reader announces that content is loading
