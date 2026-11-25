import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.navyblue.navyweather.buildlogic.libs
import com.navyblue.navyweather.buildlogic.lib
import com.navyblue.navyweather.buildlogic.navyNamespace
import com.navyblue.navyweather.buildlogic.pluginId
import com.navyblue.navyweather.buildlogic.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Shared module with Compose Multiplatform UI, Material 3, Compose Resources and Compose UI tests. */
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("navyweather.kmp.library")
        pluginManager.apply(libs.pluginId("composeMultiplatform"))
        pluginManager.apply(libs.pluginId("composeCompiler"))

        val composeDesktopCurrentOs = composeDesktopRuntimeForHost(libs.version("composeMultiplatform"))

        extensions.configure<KotlinMultiplatformExtension> {
            extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
                androidResources { enable = true }
            }

            sourceSets.getByName("commonMain").dependencies {
                implementation(libs.lib("compose-runtime"))
                implementation(libs.lib("compose-foundation"))
                implementation(libs.lib("compose-ui"))
                implementation(libs.lib("compose-material3"))
                implementation(libs.lib("compose-material-icons-core"))
                implementation(libs.lib("compose-components-resources"))
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(libs.lib("compose-ui-test"))
            }
            sourceSets.getByName("jvmTest").dependencies {
                // Skiko runtime so Compose UI tests can run on the JVM host.
                implementation(composeDesktopCurrentOs)
            }
        }

        extensions.configure<ComposeExtension> {
            extensions.configure<ResourcesExtension> {
                packageOfResClass = "$navyNamespace.resources"
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
