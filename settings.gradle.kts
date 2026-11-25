pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NavyWeather"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Core
include(":core:common")
include(":core:mvi")
include(":core:navigation")
include(":core:testing")
