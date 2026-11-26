package com.navyblue.navyweather.shared.di

import com.navyblue.navyweather.core.common.locale.LanguageProvider
import com.navyblue.navyweather.core.common.locale.SystemLanguageProvider
import com.navyblue.navyweather.core.database.di.databaseModule
import com.navyblue.navyweather.core.datastore.di.dataStoreModule
import com.navyblue.navyweather.core.network.networkModule
import com.navyblue.navyweather.core.weather.di.weatherModule
import com.navyblue.navyweather.feature.favorites.di.favoritesModule
import com.navyblue.navyweather.feature.forecast.di.forecastModule
import com.navyblue.navyweather.feature.search.di.searchModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.time.Clock

/** App-wide primitives injected instead of read statically, so tests can replace them. */
private val platformPrimitivesModule = module {
    single<Clock> { Clock.System }
    single<LanguageProvider> { SystemLanguageProvider }
}

/** Every Koin module of the app. Each feature contributes exactly one module. */
fun appModules(enableHttpLogging: Boolean): List<Module> = listOf(
    platformPrimitivesModule,
    networkModule(enableHttpLogging),
    databaseModule,
    dataStoreModule,
    weatherModule,
    favoritesModule,
    searchModule,
    forecastModule,
)

/**
 * Starts Koin once per process, from each platform's entry point
 * (Android `Application`, iOS `App` struct).
 *
 * @param platformConfig platform setup, e.g. `androidContext(...)` on Android.
 */
fun initKoin(enableHttpLogging: Boolean, platformConfig: KoinAppDeclaration = {}) {
    startKoin {
        platformConfig()
        modules(appModules(enableHttpLogging))
    }
}
