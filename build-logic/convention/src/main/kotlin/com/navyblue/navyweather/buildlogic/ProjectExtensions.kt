package com.navyblue.navyweather.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.lib(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow { IllegalArgumentException("Library '$alias' not found in libs.versions.toml") }

internal fun VersionCatalog.version(alias: String): String =
    findVersion(alias).orElseThrow { IllegalArgumentException("Version '$alias' not found in libs.versions.toml") }
        .requiredVersion

internal fun VersionCatalog.pluginId(alias: String): String =
    findPlugin(alias).orElseThrow { IllegalArgumentException("Plugin '$alias' not found in libs.versions.toml") }
        .get().pluginId

/**
 * Derives a unique Android namespace from the Gradle path:
 * `:feature:forecast` -> `com.navyblue.navyweather.feature.forecast`.
 */
internal val Project.navyNamespace: String
    get() = "com.navyblue.navyweather" + path.replace(':', '.').replace('-', '_')
