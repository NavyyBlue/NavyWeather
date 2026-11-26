package com.navyblue.navyweather.feature.search.presentation

import app.cash.turbine.test
import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.core.testing.runViewModelTest
import com.navyblue.navyweather.feature.search.FakeSearchRepository
import com.navyblue.navyweather.feature.search.domain.usecase.SearchCitiesUseCase
import com.navyblue.navyweather.feature.search.lima
import com.navyblue.navyweather.feature.search.limaOhio
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchViewModelTest {

    private val repository = FakeSearchRepository()
    private fun viewModel() = SearchViewModel(SearchCitiesUseCase(repository) { "en" })

    @Test
    fun `typing quickly searches only once for the last query`() = runViewModelTest {
        val viewModel = viewModel()

        viewModel.onIntent(SearchIntent.QueryChanged("Li"))
        advanceTimeBy(100)
        viewModel.onIntent(SearchIntent.QueryChanged("Lim"))
        advanceTimeBy(100)
        viewModel.onIntent(SearchIntent.QueryChanged("Lima"))
        advanceUntilIdle()

        assertEquals(listOf("Lima" to "en"), repository.queries)
        assertEquals(listOf(lima, limaOhio), viewModel.state.value.results)
    }

    @Test
    fun `no request before the debounce delay`() = runViewModelTest {
        val viewModel = viewModel()

        viewModel.onIntent(SearchIntent.QueryChanged("Lima"))
        advanceTimeBy(SearchViewModel.DEBOUNCE_MILLIS - 1)

        assertEquals(emptyList(), repository.queries)
    }

    @Test
    fun `retry searches again immediately after a failure`() = runViewModelTest {
        repository.result = AppResult.Failure(AppError.Network)
        val viewModel = viewModel()
        viewModel.onIntent(SearchIntent.QueryChanged("Lima"))
        advanceUntilIdle()
        assertEquals(AppError.Network, viewModel.state.value.error)

        repository.result = AppResult.Success(listOf(lima))
        viewModel.onIntent(SearchIntent.RetryClicked)
        advanceUntilIdle()

        assertEquals(listOf(lima), viewModel.state.value.results)
        assertEquals(2, repository.queries.size)
    }

    @Test
    fun `picking a city navigates to its forecast`() = runViewModelTest {
        val viewModel = viewModel()

        viewModel.effects.test {
            viewModel.onIntent(SearchIntent.CityClicked(lima))
            assertEquals(
                SearchEffect.NavigateToForecast(ForecastRoute(lima.id, "Lima", "Peru", lima.latitude, lima.longitude)),
                awaitItem(),
            )
        }
    }
}
