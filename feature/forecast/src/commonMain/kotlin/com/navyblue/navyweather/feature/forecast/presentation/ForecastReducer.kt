package com.navyblue.navyweather.feature.forecast.presentation

import com.navyblue.navyweather.core.mvi.Reducer

object ForecastReducer : Reducer<ForecastState, ForecastIntent> {

    override fun reduce(state: ForecastState, intent: ForecastIntent): ForecastState = when (intent) {
        is ForecastIntent.ForecastLoaded -> state.copy(
            forecast = intent.forecast,
            error = if (intent.forecast != null) null else state.error,
        )

        is ForecastIntent.FavoriteStatusLoaded -> state.copy(isFavorite = intent.isFavorite)
        is ForecastIntent.UnitLoaded -> state.copy(unit = intent.unit)

        // Clearing the error turns a full-screen error back into the loading state on retry.
        ForecastIntent.RefreshStarted -> state.copy(isRefreshing = true, error = null)
        ForecastIntent.RefreshSucceeded -> state.copy(isRefreshing = false)

        // Offline-first: with a cached forecast the screen keeps showing it.
        is ForecastIntent.RefreshFailed -> state.copy(
            isRefreshing = false,
            error = if (state.forecast == null) intent.error else null,
        )

        // Optimistic: the heart flips at once; the database emission confirms it.
        ForecastIntent.FavoriteToggled -> state.copy(isFavorite = !state.isFavorite)
        is ForecastIntent.UnitSelected -> state.copy(unit = intent.unit)

        ForecastIntent.RefreshRequested,
        ForecastIntent.RetryClicked,
        ForecastIntent.BackClicked,
        -> state
    }
}
