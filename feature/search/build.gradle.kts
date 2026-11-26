plugins {
    alias(libs.plugins.navyweather.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:network"))
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
        }
    }
}
