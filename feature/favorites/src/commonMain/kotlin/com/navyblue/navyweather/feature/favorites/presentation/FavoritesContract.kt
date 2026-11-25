package com.navyblue.navyweather.feature.favorites.presentation

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.mvi.UiEffect
import com.navyblue.navyweather.core.mvi.UiIntent
import com.navyblue.navyweather.core.mvi.UiState
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity

data class FavoritesState(
    val cities: List<FavoriteCity> = emptyList(),
    val isLoading: Boolean = true,
    val unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isRefreshing: Boolean = false,
) : UiState

sealed interface FavoritesIntent : UiIntent {
    // User actions
    data class CityClicked(val city: FavoriteCity) : FavoritesIntent
    data class RemoveClicked(val city: FavoriteCity) : FavoritesIntent
    data class UndoRemoveClicked(val city: FavoriteCity) : FavoritesIntent
    data object SearchClicked : FavoritesIntent
    data object RefreshRequested : FavoritesIntent

    // Internal results
    data class FavoritesLoaded(val cities: List<FavoriteCity>) : FavoritesIntent
    data class UnitLoaded(val unit: TemperatureUnit) : FavoritesIntent
    data object RefreshStarted : FavoritesIntent
    data object RefreshFinished : FavoritesIntent
}

sealed interface FavoritesEffect : UiEffect {
    data class NavigateToForecast(val route: ForecastRoute) : FavoritesEffect
    data object NavigateToSearch : FavoritesEffect

    /** Shown as a snackbar with an "Undo" action. */
    data class ShowRemoved(val city: FavoriteCity) : FavoritesEffect

    /** A pull-to-refresh failed; the cached temperatures stay on screen. */
    data class ShowRefreshError(val error: AppError) : FavoritesEffect
}
