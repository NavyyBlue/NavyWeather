import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.navyblue.navyweather.buildlogic.libs
import com.navyblue.navyweather.buildlogic.lib
import com.navyblue.navyweather.buildlogic.navyNamespace
import com.navyblue.navyweather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Shared module with Compose Multiplatform UI, Material 3 and Compose Resources. */
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("navyweather.kmp.library")
        pluginManager.apply(libs.pluginId("composeMultiplatform"))
        pluginManager.apply(libs.pluginId("composeCompiler"))

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
        }

        extensions.configure<ComposeExtension> {
            extensions.configure<ResourcesExtension> {
                packageOfResClass = "$navyNamespace.resources"
            }
        }
    }
}
