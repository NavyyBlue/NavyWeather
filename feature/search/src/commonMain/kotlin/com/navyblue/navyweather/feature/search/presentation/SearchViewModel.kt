package com.navyblue.navyweather.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.mvi.MviViewModel
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult
import com.navyblue.navyweather.feature.search.domain.usecase.SearchCitiesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchCities: SearchCitiesUseCase,
) : MviViewModel<SearchState, SearchIntent, SearchEffect>(
    initialState = SearchState(),
    reducer = SearchReducer,
) {

    private var searchJob: Job? = null

    override fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> search(intent.query, debounce = true)
            SearchIntent.RetryClicked -> search(currentState.query, debounce = false)
            is SearchIntent.CityClicked -> sendEffect(SearchEffect.NavigateToForecast(intent.city.toRoute()))
            SearchIntent.BackClicked -> sendEffect(SearchEffect.NavigateBack)
            SearchIntent.SearchStarted,
            is SearchIntent.ResultsLoaded,
            is SearchIntent.SearchFailed,
            -> Unit
        }
    }

    /**
     * Debounced while typing; a new keystroke cancels the pending search, so results always
     * belong to the latest query and the API is not hit on every letter.
     */
    private fun search(query: String, debounce: Boolean) {
        searchJob?.cancel()
        if (query.trim().length < SearchCitiesUseCase.MIN_QUERY_LENGTH) return
        searchJob = viewModelScope.launch {
            if (debounce) delay(DEBOUNCE_MILLIS)
            onIntent(SearchIntent.SearchStarted)
            when (val result = searchCities(query)) {
                is AppResult.Success -> onIntent(SearchIntent.ResultsLoaded(query, result.data))
                is AppResult.Failure -> onIntent(SearchIntent.SearchFailed(result.error))
            }
        }
    }

    private fun CitySearchResult.toRoute() = ForecastRoute(
        cityId = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
    )

    companion object {
        const val DEBOUNCE_MILLIS = 350L
    }
}
