package com.navyblue.navyweather.feature.search.presentation

import com.navyblue.navyweather.core.mvi.Reducer
import com.navyblue.navyweather.feature.search.domain.usecase.SearchCitiesUseCase

object SearchReducer : Reducer<SearchState, SearchIntent> {

    override fun reduce(state: SearchState, intent: SearchIntent): SearchState = when (intent) {
        is SearchIntent.QueryChanged ->
            if (intent.query.trim().length < SearchCitiesUseCase.MIN_QUERY_LENGTH) {
                // Too short to search: back to the idle hint.
                SearchState(query = intent.query)
            } else {
                state.copy(query = intent.query, error = null)
            }

        SearchIntent.SearchStarted -> state.copy(isLoading = true, error = null)

        is SearchIntent.ResultsLoaded -> state.copy(
            results = intent.results,
            searchedQuery = intent.query,
            isLoading = false,
            error = null,
        )

        is SearchIntent.SearchFailed -> state.copy(isLoading = false, error = intent.error, results = emptyList())

        SearchIntent.RetryClicked,
        is SearchIntent.CityClicked,
        SearchIntent.BackClicked,
        -> state
    }
}
