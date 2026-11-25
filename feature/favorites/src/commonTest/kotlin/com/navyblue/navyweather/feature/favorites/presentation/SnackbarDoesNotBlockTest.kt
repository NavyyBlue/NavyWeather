package com.navyblue.navyweather.feature.favorites.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.runComposeUiTest
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveTemperatureUnitUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RefreshFavoritesWeatherUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RemoveFavoriteUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RestoreFavoriteUseCase
import com.navyblue.navyweather.feature.favorites.fake.FakeFavoritesRepository
import com.navyblue.navyweather.feature.favorites.fake.FakeUserPreferences
import com.navyblue.navyweather.feature.favorites.fake.FixedClock
import com.navyblue.navyweather.feature.favorites.fake.TestCities
import com.navyblue.navyweather.feature.favorites.resources.Res
import com.navyblue.navyweather.feature.favorites.resources.favorites_remove_city
import com.navyblue.navyweather.feature.favorites.resources.favorites_removed
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals

/** The "removed" snackbar never blocks other interactions (specs/favorite-places). */
@OptIn(ExperimentalTestApi::class)
class SnackbarDoesNotBlockTest {

    @Test
    fun tappingAnotherFavoriteWhileTheSnackbarIsVisibleOpensItsForecast() = runComposeUiTest {
        val repository = FakeFavoritesRepository(listOf(TestCities.lima, TestCities.madrid))
        val viewModel = FavoritesViewModel(
            observeFavorites = ObserveFavoritesUseCase(repository),
            observeTemperatureUnit = ObserveTemperatureUnitUseCase(FakeUserPreferences()),
            removeFavorite = RemoveFavoriteUseCase(repository),
            restoreFavorite = RestoreFavoriteUseCase(repository),
            refreshWeather = RefreshFavoritesWeatherUseCase(repository, FixedClock(nowMillis = 0)),
        )
        val opened = mutableListOf<ForecastRoute>()
        lateinit var removeLima: String
        lateinit var limaRemoved: String
        setContent {
            removeLima = stringResource(Res.string.favorites_remove_city, TestCities.lima.name)
            limaRemoved = stringResource(Res.string.favorites_removed, TestCities.lima.name)
            NavyWeatherTheme {
                FavoritesDestination(
                    onNavigateToForecast = { opened += it },
                    onNavigateToSearch = {},
                    viewModel = viewModel,
                )
            }
        }

        waitUntil { runCatching { onNodeWithText("Lima").assertIsDisplayed() }.isSuccess }
        onNodeWithText("Lima").performCustomAccessibilityActionWithLabel(removeLima)
        waitUntil { runCatching { onNodeWithText(limaRemoved).assertIsDisplayed() }.isSuccess }

        // Pause the clock so the snackbar cannot time out before the tap.
        mainClock.autoAdvance = false
        onNodeWithText("Madrid").performClick()
        mainClock.advanceTimeBy(100)
        waitForIdle()

        onNodeWithText(limaRemoved).assertIsDisplayed() // Still visible...
        assertEquals(listOf(TestCities.madrid.id), opened.map { it.cityId }) // ...and navigation already happened.
    }
}
