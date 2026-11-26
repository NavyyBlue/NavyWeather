plugins {
    alias(libs.plugins.navyweather.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:weather"))
            implementation(project(":core:database"))
            api(project(":core:datastore"))
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(project(":core:network"))
            implementation(libs.ktor.client.mock)
        }
    }
}
