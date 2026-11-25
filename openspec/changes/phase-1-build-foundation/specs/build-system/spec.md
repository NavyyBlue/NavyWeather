# Spec Delta

## Purpose

Defines how NavyWeather is built and tested so any developer or CI machine gets the same result: pinned toolchain and versions, supported targets, shared module conventions and test commands.

## ADDED Requirements

### Requirement: Toolchain is pinned and reproducible
The build SHALL use a pinned Gradle version through the wrapper and SHALL run the Gradle daemon on JDK 25, so a fresh clone builds with the same toolchain on every machine.

#### Scenario: Fresh clone
- **WHEN** a developer with JDK 25, the Android SDK and Xcode clones the repository and runs `./gradlew build`
- **THEN** the wrapper downloads the pinned Gradle version and the build runs on a JDK 25 daemon without any local configuration besides the Android SDK location

#### Scenario: Missing JDK 25
- **WHEN** the machine has no JDK 25 installed
- **THEN** the build stops with an explicit message that JDK 25 is required instead of failing later with an unrelated error

### Requirement: Dependency versions are declared in one catalog
Every library and plugin version SHALL be declared once, as an exact version, in the version catalog, and modules SHALL reference dependencies only through that catalog.

#### Scenario: Upgrading a library
- **WHEN** a developer changes a library version in the catalog
- **THEN** every module that uses the library builds against the new version without editing module build files

### Requirement: Shared modules target Android and iOS
Every shared module SHALL compile for Android, iOS devices (arm64) and the Apple Silicon iOS simulator, with a minimum Android API of 26.

#### Scenario: Compile all targets
- **WHEN** the shared modules are compiled
- **THEN** Android, iOS device and iOS simulator outputs are produced for each of them

### Requirement: Shared tests run on the JVM and on the iOS simulator
Tests written once in shared test sources SHALL run on a JVM host without an emulator and on the iOS simulator; the JVM target SHALL exist only to run tests and SHALL NOT be shipped.

#### Scenario: Run all tests
- **WHEN** a developer runs `./gradlew allTests` on a Mac with an iOS simulator available
- **THEN** every shared test runs on the JVM and on the iOS simulator and the build fails if any of them fails

#### Scenario: Fast feedback
- **WHEN** a developer runs `./gradlew jvmTest`
- **THEN** shared tests run on the JVM only, without starting an emulator or simulator

### Requirement: Modules declare their role, not their configuration
A module SHALL obtain its targets, compiler settings, Android namespace and standard dependencies by applying one convention plugin for its role (library, UI library, feature, or local database), so adding a module needs only its own dependencies.

#### Scenario: New feature module
- **WHEN** a developer creates a feature module that applies the feature convention
- **THEN** it compiles for all targets with the UI, MVI, navigation, dependency injection and testing setup already available

#### Scenario: Unique namespaces
- **WHEN** two modules are built
- **THEN** each gets a distinct Android namespace derived from its module path
