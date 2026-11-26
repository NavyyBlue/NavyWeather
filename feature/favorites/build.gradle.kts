plugins {
    alias(libs.plugins.navyweather.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:database"))
            implementation(project(":core:weather"))
            api(project(":core:datastore"))
        }
    }
}
