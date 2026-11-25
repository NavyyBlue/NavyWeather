import com.navyblue.navyweather.buildlogic.libs
import com.navyblue.navyweather.buildlogic.lib
import com.navyblue.navyweather.buildlogic.pluginId
import com.navyblue.navyweather.buildlogic.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * A feature module: Compose UI + MVI + Koin + Navigation, already wired to the core
 * modules every feature needs, plus the full testing toolbox.
 *
 * A feature never depends on another feature (enforced by Konsist).
 */
class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("navyweather.kmp.compose")
        pluginManager.apply(libs.pluginId("kotlinSerialization"))
        pluginManager.apply(libs.pluginId("mokkery"))

        val composeDesktopCurrentOs = composeDesktopRuntimeForHost(libs.version("composeMultiplatform"))

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(project(":core:common"))
                implementation(project(":core:mvi"))
                implementation(project(":core:designsystem"))
                implementation(project(":core:navigation"))

                implementation(libs.lib("koin-core"))
                implementation(libs.lib("koin-compose"))
                implementation(libs.lib("koin-compose-viewmodel"))
                implementation(libs.lib("androidx-lifecycle-viewmodel-compose"))
                implementation(libs.lib("androidx-lifecycle-runtime-compose"))
                implementation(libs.lib("androidx-navigation-compose"))
                implementation(libs.lib("kotlinx-serialization-json"))
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(project(":core:testing"))
                implementation(libs.lib("turbine"))
                implementation(libs.lib("compose-ui-test"))
            }
            sourceSets.getByName("jvmTest").dependencies {
                // Skiko runtime so Compose UI tests can run on the JVM host.
                implementation(composeDesktopCurrentOs)
            }
        }
    }
}

/** Equivalent to `compose.desktop.currentOs`, resolved for the machine running the tests. */
private fun composeDesktopRuntimeForHost(version: String): String {
    val osName = System.getProperty("os.name").lowercase()
    val os = when {
        "mac" in osName -> "macos"
        "win" in osName -> "windows"
        else -> "linux"
    }
    val arch = if (System.getProperty("os.arch") in setOf("aarch64", "arm64")) "arm64" else "x64"
    return "org.jetbrains.compose.desktop:desktop-jvm-$os-$arch:$version"
}
