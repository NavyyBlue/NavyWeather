# Spec Delta

## MODIFIED Requirements

### Requirement: One shared Material 3 Expressive theme
All screens SHALL use one theme from the shared design system, based on Material 3 Expressive, with its spring-based motion, its extended corner scale (4, 8, 12, 16, 20, 28, 32 and 48 dp), a shared spacing scale, the Google Sans Flex typeface for every type role on both platforms, and numbers that keep the same width so changing values do not shift the layout. The typeface SHALL ship with its license.

#### Scenario: Feature screen styling
- **WHEN** a feature needs a color, type style, shape or spacing value
- **THEN** it takes it from the shared theme instead of defining a local value

#### Scenario: Same typeface on both platforms
- **WHEN** any text is shown on Android or iOS
- **THEN** it is rendered with Google Sans Flex instead of the platform's system font
