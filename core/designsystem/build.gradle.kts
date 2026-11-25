plugins {
    alias(libs.plugins.navyweather.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:common"))
            implementation(libs.material.kolor)
        }
    }
}
