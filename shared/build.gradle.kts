import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.navyweather.kmp.compose)
    // typed routes: toRoute<T>() needs the serialization plugin at the call site on Kotlin/Native.
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // iOS framework consumed by the Xcode project (iosApp). Static: one binary, faster app launch.
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:navigation"))
            implementation(project(":core:network"))
            implementation(project(":core:database"))
            implementation(project(":core:datastore"))
            implementation(project(":core:weather"))

            implementation(project(":feature:favorites"))
            implementation(project(":feature:search"))
            implementation(project(":feature:forecast"))

            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
        jvmTest.dependencies {
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
        }
    }
}
