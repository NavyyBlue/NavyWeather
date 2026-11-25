plugins {
    alias(libs.plugins.navyweather.kmp.library)
    alias(libs.plugins.navyweather.room)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(project(":core:testing"))
        }
    }
}
