package com.navyblue.navyweather.feature.favorites.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import com.navyblue.navyweather.feature.favorites.resources.favorites_temperature_unavailable
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.feature.favorites.fake.TestCities
import com.navyblue.navyweather.feature.favorites.resources.Res
import com.navyblue.navyweather.feature.favorites.resources.favorites_empty_title
import com.navyblue.navyweather.feature.favorites.resources.favorites_remove_city
import com.navyblue.navyweather.feature.favorites.resources.favorites_search_city
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Compose UI test written once in commonTest; runs on the JVM host and the iOS simulator.
 * Expected texts are read from the same string resources, so the test passes in any locale.
 */
@OptIn(ExperimentalTestApi::class)
class FavoritesScreenTest {

    @Test
    fun emptyStateInvitesToSearch() = runComposeUiTest {
        val intents = mutableListOf<FavoritesIntent>()
        lateinit var emptyTitle: String
        lateinit var searchCity: String
        setContent {
            emptyTitle = stringResource(Res.string.favorites_empty_title)
            searchCity = stringResource(Res.string.favorites_search_city)
            NavyWeatherTheme {
                FavoritesScreen(state = FavoritesState(isLoading = false), onIntent = { intents += it })
            }
        }

        onNodeWithText(emptyTitle).assertIsDisplayed()
        // Only the empty state's button: no floating action button competes with it.
        onAllNodesWithText(searchCity).assertCountEquals(1)
        onNodeWithText(searchCity).performClick()

        assertEquals(listOf<FavoritesIntent>(FavoritesIntent.SearchClicked), intents)
    }

    @Test
    fun rowsShowTheTemperatureInTheSelectedUnit() = runComposeUiTest {
        setContent {
            NavyWeatherTheme {
                FavoritesScreen(
                    state = FavoritesState(
                        cities = listOf(TestCities.lima.copy(temperatureCelsius = 22.0)),
                        isLoading = false,
                        unit = TemperatureUnit.FAHRENHEIT,
                    ),
                    onIntent = {},
                )
            }
        }

        onNodeWithText("72°").assertIsDisplayed()
    }

    @Test
    fun listShowsCitiesAndReportsActions() = runComposeUiTest {
        val intents = mutableListOf<FavoritesIntent>()
        lateinit var removeLima: String
        setContent {
            removeLima = stringResource(Res.string.favorites_remove_city, TestCities.lima.name)
            NavyWeatherTheme {
                FavoritesScreen(
                    state = FavoritesState(cities = listOf(TestCities.lima, TestCities.madrid), isLoading = false),
                    onIntent = { intents += it },
                )
            }
        }

        onNodeWithText("Lima").assertIsDisplayed()
        onNodeWithText("Spain").assertIsDisplayed()

        onNodeWithText("Madrid").performClick()
        // Removing is a swipe; the same action is exposed to screen readers as a custom action.
        onNodeWithText("Lima").performCustomAccessibilityActionWithLabel(removeLima)

        assertEquals(
            listOf(FavoritesIntent.CityClicked(TestCities.madrid), FavoritesIntent.RemoveClicked(TestCities.lima)),
            intents,
        )
    }

    @Test
    fun listShowsTheSearchFloatingActionButton() = runComposeUiTest {
        val intents = mutableListOf<FavoritesIntent>()
        lateinit var searchCity: String
        setContent {
            searchCity = stringResource(Res.string.favorites_search_city)
            NavyWeatherTheme {
                FavoritesScreen(
                    state = FavoritesState(cities = listOf(TestCities.lima), isLoading = false),
                    onIntent = { intents += it },
                )
            }
        }

        // The FAB merges its icon and label into one node; the label lives in the unmerged tree.
        onNodeWithText(searchCity, useUnmergedTree = true).performClick()

        assertEquals(listOf<FavoritesIntent>(FavoritesIntent.SearchClicked), intents)
    }

    @Test
    fun missingTemperatureShowsADashAnnouncedAsNotAvailable() = runComposeUiTest {
        lateinit var unavailable: String
        setContent {
            unavailable = stringResource(Res.string.favorites_temperature_unavailable)
            NavyWeatherTheme {
                FavoritesScreen(
                    state = FavoritesState(cities = listOf(TestCities.lima.copy(temperatureCelsius = null)), isLoading = false),
                    onIntent = {},
                )
            }
        }

        onNodeWithContentDescription(unavailable, useUnmergedTree = true).assertTextEquals("–")
    }
}
