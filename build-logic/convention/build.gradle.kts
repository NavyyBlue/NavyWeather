plugins {
    `kotlin-dsl`
}

group = "com.navyblue.navyweather.buildlogic"

dependencies {
    compileOnly(libs.gradlePlugin.kotlin)
    compileOnly(libs.gradlePlugin.android)
    compileOnly(libs.gradlePlugin.compose)
    compileOnly(libs.gradlePlugin.composeCompiler)
    compileOnly(libs.gradlePlugin.ksp)
    compileOnly(libs.gradlePlugin.room)
    compileOnly(libs.gradlePlugin.mokkery)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "navyweather.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = "navyweather.kmp.compose"
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("kmpFeature") {
            id = "navyweather.kmp.feature"
            implementationClass = "KmpFeatureConventionPlugin"
        }
        register("room") {
            id = "navyweather.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}
