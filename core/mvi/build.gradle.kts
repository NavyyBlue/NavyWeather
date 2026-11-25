plugins {
    alias(libs.plugins.navyweather.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        commonTest.dependencies {
            implementation(project(":core:testing"))
        }
    }
}
