import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.navyblue.navyweather.buildlogic.libs
import com.navyblue.navyweather.buildlogic.lib
import com.navyblue.navyweather.buildlogic.navyNamespace
import com.navyblue.navyweather.buildlogic.pluginId
import com.navyblue.navyweather.buildlogic.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.process.CommandLineArgumentProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Base for every shared module.
 *
 * Targets:
 *  - android: production target (AGP 9 `com.android.kotlin.multiplatform.library`).
 *  - iosArm64 / iosSimulatorArm64: production targets (device + Apple Silicon simulator).
 *  - jvm: NOT shipped. It exists only as a fast host to run `commonTest`
 *    (Room, Compose UI tests, Koin verify) without an emulator.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("kotlinMultiplatform"))
        pluginManager.apply(libs.pluginId("androidMultiplatformLibrary"))

        extensions.configure<KotlinMultiplatformExtension> {
            extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
                namespace = navyNamespace
                compileSdk = libs.version("androidCompileSdk").toInt()
                minSdk = libs.version("androidMinSdk").toInt()
            }

            jvm()
            iosArm64()
            iosSimulatorArm64()

            compilerOptions {
                // `expect object` is used for Room's database constructor.
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }

            sourceSets.getByName("commonMain").dependencies {
                implementation(libs.lib("kotlinx-coroutines-core"))
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(kotlin("test"))
                implementation(libs.lib("kotlinx-coroutines-test"))
            }
        }

        // JVM test host on recent JDKs: the bundled SQLite driver loads a native library and
        // DataStore's bundled protobuf uses sun.misc.Unsafe; both only print warnings, which these
        // flags silence. They are added only on the JDKs that know them, so older JDKs still start.
        tasks.withType<Test>().configureEach {
            val testJdk = javaLauncher.map { it.metadata.languageVersion.asInt() }
            jvmArgumentProviders.add(
                CommandLineArgumentProvider {
                    val jdk = testJdk.getOrElse(Runtime.version().feature())
                    buildList {
                        if (jdk >= 22) add("--enable-native-access=ALL-UNNAMED")
                        if (jdk >= 23) add("--sun-misc-unsafe-memory-access=allow")
                    }
                },
            )
        }
    }
}
