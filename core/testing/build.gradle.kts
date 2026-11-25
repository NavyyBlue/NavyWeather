plugins {
    alias(libs.plugins.navyweather.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:common"))
            api(kotlin("test"))
            api(libs.kotlinx.coroutines.test)
            api(libs.turbine)
        }
    }
}
