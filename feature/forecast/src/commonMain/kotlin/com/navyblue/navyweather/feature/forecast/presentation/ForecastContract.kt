package com.navyblue.navyweather.feature.forecast.presentation

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.mvi.UiEffect
import com.navyblue.navyweather.core.mvi.UiIntent
import com.navyblue.navyweather.core.mvi.UiState
import com.navyblue.navyweather.feature.forecast.domain.model.Forecast
import com.navyblue.navyweather.feature.forecast.domain.model.Location

data class ForecastState(
    val location: Location,
    val forecast: Forecast? = null,
    val unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isFavorite: Boolean = false,
    val isRefreshing: Boolean = false,
    /** Only set when there is nothing cached to show; with cached data errors go to a snackbar. */
    val error: AppError? = null,
) : UiState {
    val isInitialLoading: Boolean get() = forecast == null && error == null
}

sealed interface ForecastIntent : UiIntent {
    // User actions
    data object RefreshRequested : ForecastIntent
    data object RetryClicked : ForecastIntent
    data object FavoriteToggled : ForecastIntent
    data class UnitSelected(val unit: TemperatureUnit) : ForecastIntent
    data object BackClicked : ForecastIntent

    // Internal results
    data class ForecastLoaded(val forecast: Forecast?) : ForecastIntent
    data class FavoriteStatusLoaded(val isFavorite: Boolean) : ForecastIntent
    data class UnitLoaded(val unit: TemperatureUnit) : ForecastIntent
    data object RefreshStarted : ForecastIntent
    data object RefreshSucceeded : ForecastIntent
    data class RefreshFailed(val error: AppError) : ForecastIntent
}

sealed interface ForecastEffect : UiEffect {
    data object NavigateBack : ForecastEffect

    /** Refresh failed but cached data is still on screen. */
    data class ShowRefreshError(val error: AppError) : ForecastEffect
}
