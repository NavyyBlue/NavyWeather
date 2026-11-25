plugins {
    alias(libs.plugins.navyweather.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:common"))
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            api(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            api(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
        }
    }
}
