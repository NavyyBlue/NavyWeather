plugins {
    alias(libs.plugins.navyweather.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.datastore.preferences.core)
            implementation(libs.okio)
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
