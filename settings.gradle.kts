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
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:weather")
include(":core:designsystem")

// Features
include(":feature:favorites")
include(":feature:search")
include(":feature:forecast")
include(":shared")

// Apps
include(":androidApp")
