package com.navyblue.navyweather.feature.forecast.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.feature.forecast.domain.model.WeatherCondition
import com.navyblue.navyweather.feature.forecast.limaLocation
import com.navyblue.navyweather.feature.forecast.resources.Res
import com.navyblue.navyweather.feature.forecast.resources.condition_partly_cloudy
import com.navyblue.navyweather.feature.forecast.resources.condition_unknown
import com.navyblue.navyweather.feature.forecast.resources.forecast_max_min
import com.navyblue.navyweather.feature.forecast.resources.forecast_remove_favorite
import com.navyblue.navyweather.feature.forecast.resources.forecast_today
import com.navyblue.navyweather.feature.forecast.weekForecast
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Forecast screen details (specs/weather-forecast). Expected texts come from the same string
 * resources, so the tests pass in any locale; they run on the JVM and the iOS simulator.
 */
@OptIn(ExperimentalTestApi::class)
class ForecastScreenDetailsTest {

    private fun state(unit: TemperatureUnit = TemperatureUnit.CELSIUS, condition: WeatherCondition = WeatherCondition.PARTLY_CLOUDY) =
        ForecastState(location = limaLocation, forecast = weekForecast(21.6, condition), unit = unit)

    @Test
    fun heroShowsTemperatureUnitConditionAndRangeAsOneElement() = runComposeUiTest {
        lateinit var partlyCloudy: String
        lateinit var range: String
        setContent {
            partlyCloudy = stringResource(Res.string.condition_partly_cloudy)
            range = stringResource(Res.string.forecast_max_min, "24°", "16°")
            NavyWeatherTheme { ForecastScreen(state = state(), onIntent = {}) }
        }

        onNodeWithTag(HERO_TEMPERATURE_TAG, useUnmergedTree = true).assertTextEquals("22")
        onNodeWithText(range, useUnmergedTree = true).assertIsDisplayed()
        // Screen readers get the hero as a single element.
        onNodeWithContentDescription("22°C, $partlyCloudy").assertIsDisplayed()
    }

    @Test
    fun sevenDaysAreListedWithTodayFirst() = runComposeUiTest {
        lateinit var today: String
        setContent {
            today = stringResource(Res.string.forecast_today)
            NavyWeatherTheme {
                Box(Modifier.requiredSize(width = 400.dp, height = 2000.dp)) { ForecastScreen(state = state(), onIntent = {}) }
            }
        }

        // The tall test layout extends past the window, so check presence rather than visibility.
        onNodeWithText(today, useUnmergedTree = true).assertExists()
        // Every day shows its low and high: 7 rows plus today's range in the hero.
        assertEquals(7, onAllNodesWithText("16°", useUnmergedTree = true).fetchSemanticsNodes().size)
    }

    @Test
    fun fahrenheitAppliesToEveryTemperature() = runComposeUiTest {
        setContent {
            NavyWeatherTheme {
                Box(Modifier.requiredSize(width = 400.dp, height = 2000.dp)) {
                    ForecastScreen(state = state(unit = TemperatureUnit.FAHRENHEIT), onIntent = {})
                }
            }
        }

        onNodeWithTag(HERO_TEMPERATURE_TAG, useUnmergedTree = true).assertTextEquals("71") // 21.6 °C
        assertEquals(0, onAllNodesWithText("16°", useUnmergedTree = true).fetchSemanticsNodes().size)
        assertEquals(7, onAllNodesWithText("61°", useUnmergedTree = true).fetchSemanticsNodes().size) // 16 °C
    }

    @Test
    fun savedPlaceOffersRemovalFromFavorites() = runComposeUiTest {
        val intents = mutableListOf<ForecastIntent>()
        lateinit var removeFavorite: String
        setContent {
            removeFavorite = stringResource(Res.string.forecast_remove_favorite)
            NavyWeatherTheme { ForecastScreen(state = state().copy(isFavorite = true), onIntent = { intents += it }) }
        }

        onNodeWithContentDescription(removeFavorite).performClick()

        assertEquals(listOf<ForecastIntent>(ForecastIntent.FavoriteToggled), intents)
    }

    @Test
    fun unknownWeatherCodeReadsUnknownConditions() = runComposeUiTest {
        lateinit var unknown: String
        setContent {
            unknown = stringResource(Res.string.condition_unknown)
            NavyWeatherTheme {
                ForecastScreen(state = state(condition = WeatherCondition.fromWmoCode(42)), onIntent = {})
            }
        }

        onNodeWithText(unknown, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun wideWindowsShowConditionsAndDaysSideBySide() = runComposeUiTest {
        lateinit var today: String
        setContent {
            today = stringResource(Res.string.forecast_today)
            NavyWeatherTheme {
                Box(Modifier.requiredSize(width = 1000.dp, height = 700.dp)) { ForecastScreen(state = state(), onIntent = {}) }
            }
        }

        assertNotNull(onNodeWithTag(FORECAST_DAYS_TAG).fetchSemanticsNode())
        val hero = onNodeWithTag(HERO_CARD_TAG, useUnmergedTree = true).getBoundsInRoot()
        val todayRow = onNodeWithText(today, useUnmergedTree = true).getBoundsInRoot()
        assertTrue(todayRow.left >= hero.right, "the day list is to the right of the hero")
    }

    @Test
    fun narrowWindowsShowOneColumn() = runComposeUiTest {
        lateinit var today: String
        setContent {
            today = stringResource(Res.string.forecast_today)
            NavyWeatherTheme {
                Box(Modifier.requiredSize(width = 400.dp, height = 2000.dp)) { ForecastScreen(state = state(), onIntent = {}) }
            }
        }

        onNodeWithTag(FORECAST_DAYS_TAG).assertDoesNotExist()
        val hero = onNodeWithTag(HERO_CARD_TAG, useUnmergedTree = true).getBoundsInRoot()
        val todayRow = onNodeWithText(today, useUnmergedTree = true).getBoundsInRoot()
        assertTrue(todayRow.top >= hero.bottom, "the day list is below the hero")
    }
}
