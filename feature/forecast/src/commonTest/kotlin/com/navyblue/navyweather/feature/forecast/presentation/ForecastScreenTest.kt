package com.navyblue.navyweather.feature.forecast.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.feature.forecast.limaLocation
import com.navyblue.navyweather.feature.forecast.resources.Res
import com.navyblue.navyweather.feature.forecast.resources.forecast_add_favorite
import com.navyblue.navyweather.feature.forecast.resources.forecast_fahrenheit
import com.navyblue.navyweather.feature.forecast.sampleForecast
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ForecastScreenTest {

    @Test
    fun heroShowsTemperatureInTheSelectedUnitAndReportsActions() = runComposeUiTest {
        val intents = mutableListOf<ForecastIntent>()
        lateinit var addFavorite: String
        lateinit var fahrenheit: String
        setContent {
            addFavorite = stringResource(Res.string.forecast_add_favorite)
            fahrenheit = stringResource(Res.string.forecast_fahrenheit)
            NavyWeatherTheme {
                ForecastScreen(
                    state = ForecastState(location = limaLocation, forecast = sampleForecast(22.0), unit = TemperatureUnit.FAHRENHEIT),
                    onIntent = { intents += it },
                )
            }
        }

        // The hero merges its semantics for screen readers, so the number lives in the unmerged tree.
        onNodeWithTag(HERO_TEMPERATURE_TAG, useUnmergedTree = true).assertTextEquals("72")

        onNodeWithContentDescription(addFavorite).performClick()
        onNodeWithContentDescription(fahrenheit).performClick()

        assertEquals(
            listOf(ForecastIntent.FavoriteToggled, ForecastIntent.UnitSelected(TemperatureUnit.FAHRENHEIT)),
            intents,
        )
    }
}
