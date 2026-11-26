package com.navyblue.navyweather.shared

import androidx.lifecycle.SavedStateHandle
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.shared.di.appModules
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

/**
 * Koin resolves the graph at runtime, so a missing definition would only crash when a screen
 * opens. `verify()` checks every constructor dependency of every definition at test time.
 * (JVM only: `verify()` uses reflection.)
 */
class KoinGraphTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `every definition in the app graph can be resolved`() {
        module { includes(appModules(enableHttpLogging = false)) }.verify(
            extraTypes = listOf(
                HttpClientEngine::class,
                SavedStateHandle::class,
                // Passed at runtime with parametersOf(route) from the navigation entry.
                ForecastRoute::class,
            ),
        )
    }
}
