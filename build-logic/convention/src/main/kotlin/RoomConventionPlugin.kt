import androidx.room.gradle.RoomExtension
import com.navyblue.navyweather.buildlogic.libs
import com.navyblue.navyweather.buildlogic.lib
import com.navyblue.navyweather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Room KMP + bundled SQLite. KSP generates the DAOs for every target. */
class RoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("ksp"))
        pluginManager.apply(libs.pluginId("room"))

        extensions.configure<RoomExtension> {
            // Exported schemas are versioned in git to review and test migrations.
            schemaDirectory("$projectDir/schemas")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                api(libs.lib("room-runtime"))
                implementation(libs.lib("sqlite-bundled"))
            }
        }

        dependencies {
            listOf("kspAndroid", "kspJvm", "kspIosArm64", "kspIosSimulatorArm64").forEach { configuration ->
                add(configuration, libs.lib("room-compiler"))
            }
        }
    }
}
