package com.navyblue.navyweather.feature.search.presentation

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.mvi.UiEffect
import com.navyblue.navyweather.core.mvi.UiIntent
import com.navyblue.navyweather.core.mvi.UiState
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult

data class SearchState(
    val query: String = "",
    val results: List<CitySearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null,
    /** The query the current [results] belong to; `null` until a search has completed. */
    val searchedQuery: String? = null,
) : UiState {
    val showNoResults: Boolean get() = searchedQuery != null && results.isEmpty() && !isLoading && error == null
}

sealed interface SearchIntent : UiIntent {
    // User actions
    data class QueryChanged(val query: String) : SearchIntent
    data object RetryClicked : SearchIntent
    data class CityClicked(val city: CitySearchResult) : SearchIntent
    data object BackClicked : SearchIntent

    // Internal results
    data object SearchStarted : SearchIntent
    data class ResultsLoaded(val query: String, val results: List<CitySearchResult>) : SearchIntent
    data class SearchFailed(val error: AppError) : SearchIntent
}

sealed interface SearchEffect : UiEffect {
    data class NavigateToForecast(val route: ForecastRoute) : SearchEffect
    data object NavigateBack : SearchEffect
}
