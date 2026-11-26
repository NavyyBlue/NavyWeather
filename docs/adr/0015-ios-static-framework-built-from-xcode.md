# ADR-0015: iOS static framework built from Xcode

- **Status:** Accepted
- **Date:** 2026-11-25
- **Phase:** 3 — Favorites and app shell (`phase-3-favorites-app-shell`)

## Context

The iOS app is a SwiftUI shell that hosts the shared Compose UI. Xcode has to build and embed the Kotlin code, and a developer should be able to press Run in Xcode without a separate manual Gradle step.

## Decision

`shared` produces a **static framework** named `Shared`. The Xcode project runs a **Run Script build phase**: `cd "$SRCROOT/.." && ./gradlew :shared:embedAndSignAppleFrameworkForXcode`, which builds the framework for the selected configuration and architecture. `ENABLE_USER_SCRIPT_SANDBOXING = NO` lets the script reach the Gradle project. Swift calls two entry points: `startKoinIos()` once in the `App` initializer, and `MainViewController()`, wrapped in a `UIViewControllerRepresentable` that ignores the safe area because Compose handles insets itself.

## Alternatives considered

| Option | Why it was not chosen |
|---|---|
| CocoaPods integration | Adds Ruby tooling and a Podfile for a single local framework. |
| Swift Package with an XCFramework | Good for distributing to other teams, but requires building and publishing the XCFramework before every Xcode run. |
| Dynamic framework | Longer app launch and an extra embedding step, with no benefit for a single app target. |

## Consequences

- Positive: Run in Xcode always builds the current Kotlin code, and there is only one binary to load at launch.
- Negative: an Xcode build depends on Gradle and on JDK 25 being available to the script. This is documented in the README.
- Negative: user script sandboxing is disabled for the project. Only this build phase relies on it.
