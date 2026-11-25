package com.navyblue.navyweather.feature.favorites.presentation

import app.cash.turbine.test
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.core.testing.runViewModelTest
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RemoveFavoriteUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RestoreFavoriteUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveTemperatureUnitUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RefreshFavoritesWeatherUseCase
import com.navyblue.navyweather.feature.favorites.fake.FakeFavoritesRepository
import com.navyblue.navyweather.feature.favorites.fake.FakeUserPreferences
import com.navyblue.navyweather.feature.favorites.fake.FixedClock
import com.navyblue.navyweather.feature.favorites.fake.TestCities
import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FavoritesViewModelTest {

    private val preferences = FakeUserPreferences()

    private fun viewModel(repository: FakeFavoritesRepository) = FavoritesViewModel(
        observeFavorites = ObserveFavoritesUseCase(repository),
        observeTemperatureUnit = ObserveTemperatureUnitUseCase(preferences),
        removeFavorite = RemoveFavoriteUseCase(repository),
        restoreFavorite = RestoreFavoriteUseCase(repository),
        refreshWeather = RefreshFavoritesWeatherUseCase(repository, FixedClock(nowMillis = 1_000_000_000)),
    )

    @Test
    fun `starts loading and then shows favorites sorted`() = runViewModelTest {
        val viewModel = viewModel(FakeFavoritesRepository(listOf(TestCities.madrid, TestCities.lima)))
        assertTrue(viewModel.state.value.isLoading)

        advanceUntilIdle()

        assertEquals(listOf("Lima", "Madrid"), viewModel.state.value.cities.map { it.name })
    }

    @Test
    fun `remove deletes the city and offers undo`() = runViewModelTest {
        val repository = FakeFavoritesRepository(listOf(TestCities.lima, TestCities.madrid))
        val viewModel = viewModel(repository)

        viewModel.effects.test {
            viewModel.onIntent(FavoritesIntent.RemoveClicked(TestCities.lima))

            assertEquals(FavoritesEffect.ShowRemoved(TestCities.lima), awaitItem())
            assertEquals(listOf("Madrid"), viewModel.state.value.cities.map { it.name })
        }
    }

    @Test
    fun `undo restores the removed city`() = runViewModelTest {
        val viewModel = viewModel(FakeFavoritesRepository(listOf(TestCities.lima, TestCities.madrid)))
        advanceUntilIdle()

        viewModel.onIntent(FavoritesIntent.RemoveClicked(TestCities.lima))
        advanceUntilIdle()
        assertEquals(listOf("Madrid"), viewModel.state.value.cities.map { it.name })

        viewModel.onIntent(FavoritesIntent.UndoRemoveClicked(TestCities.lima))
        advanceUntilIdle()
        assertEquals(listOf("Lima", "Madrid"), viewModel.state.value.cities.map { it.name })
    }

    @Test
    fun `opening the list downloads missing temperatures`() = runViewModelTest {
        val repository = FakeFavoritesRepository(listOf(TestCities.lima), refreshedTemperature = 23.4)
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(listOf(TestCities.lima.id), repository.refreshedIds)
        assertEquals(23.4, viewModel.state.value.cities.single().temperatureCelsius)
        assertEquals(false, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `failed pull to refresh shows a snackbar`() = runViewModelTest {
        val repository = FakeFavoritesRepository(listOf(TestCities.lima), refreshResult = AppResult.Failure(AppError.Network))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onIntent(FavoritesIntent.RefreshRequested)
            assertEquals(FavoritesEffect.ShowRefreshError(AppError.Network), awaitItem())
        }
    }

    @Test
    fun `clicking a city navigates to its forecast`() = runViewModelTest {
        val viewModel = viewModel(FakeFavoritesRepository(listOf(TestCities.cusco)))

        viewModel.effects.test {
            viewModel.onIntent(FavoritesIntent.CityClicked(TestCities.cusco))

            val expected = ForecastRoute(
                cityId = TestCities.cusco.id,
                name = "Cusco",
                country = "Peru",
                latitude = TestCities.cusco.latitude,
                longitude = TestCities.cusco.longitude,
            )
            assertEquals(FavoritesEffect.NavigateToForecast(expected), awaitItem())
        }
    }

    @Test
    fun `failed background refresh stays silent and keeps the list`() = runViewModelTest {
        val repository = FakeFavoritesRepository(listOf(TestCities.lima), refreshResult = AppResult.Failure(AppError.Network))
        val viewModel = viewModel(repository)

        viewModel.effects.test {
            advanceUntilIdle()

            assertEquals(listOf(TestCities.lima.id), repository.refreshedIds) // It did try.
            expectNoEvents()
        }
        assertEquals(listOf("Lima"), viewModel.state.value.cities.map { it.name })
    }

    @Test
    fun `only one refresh runs at a time`() = runViewModelTest {
        val repository = FakeFavoritesRepository(listOf(TestCities.lima), refreshDelayMillis = 1_000)
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        repository.refreshedIds.clear()

        viewModel.onIntent(FavoritesIntent.RefreshRequested)
        viewModel.onIntent(FavoritesIntent.RefreshRequested)
        advanceUntilIdle()

        assertEquals(listOf(TestCities.lima.id), repository.refreshedIds)
    }
}
