package com.navyblue.navyweather.feature.forecast.presentation

import app.cash.turbine.test
import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.testing.runViewModelTest
import com.navyblue.navyweather.feature.forecast.FakeFavoriteStatusRepository
import com.navyblue.navyweather.feature.forecast.FakeForecastRepository
import com.navyblue.navyweather.feature.forecast.FakeUserPreferences
import com.navyblue.navyweather.feature.forecast.FixedClock
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveFavoriteStatusUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveForecastUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveTemperatureUnitUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.RefreshForecastUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.SetTemperatureUnitUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ToggleFavoriteUseCase
import com.navyblue.navyweather.feature.forecast.limaRoute
import com.navyblue.navyweather.feature.forecast.sampleForecast
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ForecastViewModelTest {

    private val preferences = FakeUserPreferences()
    private val favorites = FakeFavoriteStatusRepository()

    private fun viewModel(repository: FakeForecastRepository) = ForecastViewModel(
        route = limaRoute,
        observeForecast = ObserveForecastUseCase(repository),
        observeFavoriteStatus = ObserveFavoriteStatusUseCase(favorites),
        observeTemperatureUnit = ObserveTemperatureUnitUseCase(preferences),
        refreshForecast = RefreshForecastUseCase(repository, FixedClock(nowMillis = 1_000_000_000)),
        toggleFavorite = ToggleFavoriteUseCase(favorites),
        setTemperatureUnit = SetTemperatureUnitUseCase(preferences),
    )

    @Test
    fun `opening a city without cache downloads and shows the forecast`() = runViewModelTest {
        val repository = FakeForecastRepository(initial = null)
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(1, repository.refreshCalls)
        assertNotNull(viewModel.state.value.forecast)
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `refresh failure with cached data keeps it and shows a snackbar`() = runViewModelTest {
        val repository = FakeForecastRepository(initial = sampleForecast(), refreshResult = AppResult.Failure(AppError.Network))
        val viewModel = viewModel(repository)

        viewModel.effects.test {
            assertEquals(ForecastEffect.ShowRefreshError(AppError.Network), awaitItem())
        }
        assertNotNull(viewModel.state.value.forecast)
    }

    @Test
    fun `favorite toggle is persisted`() = runViewModelTest {
        val viewModel = viewModel(FakeForecastRepository(initial = sampleForecast()))
        advanceUntilIdle()

        viewModel.onIntent(ForecastIntent.FavoriteToggled)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isFavorite)

        viewModel.onIntent(ForecastIntent.FavoriteToggled)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isFavorite)
    }

    @Test
    fun `unit selection is saved in preferences`() = runViewModelTest {
        val viewModel = viewModel(FakeForecastRepository(initial = sampleForecast()))

        viewModel.onIntent(ForecastIntent.UnitSelected(TemperatureUnit.FAHRENHEIT))
        advanceUntilIdle()

        assertEquals(TemperatureUnit.FAHRENHEIT, preferences.temperatureUnit.value)
        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.state.value.unit)
    }

    @Test
    fun `pull to refresh during the opening refresh runs right after it`() = runViewModelTest {
        val repository = FakeForecastRepository(initial = null, refreshDelayMillis = 1_000)
        val viewModel = viewModel(repository)
        runCurrent() // The opening (non-forced) refresh is downloading.

        viewModel.onIntent(ForecastIntent.RefreshRequested)
        advanceUntilIdle()

        assertEquals(2, repository.refreshCalls)
        assertEquals(false, viewModel.state.value.isRefreshing)
    }
}
