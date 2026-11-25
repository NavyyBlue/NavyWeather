package com.navyblue.navyweather.feature.favorites.presentation

import com.navyblue.navyweather.core.mvi.Reducer

/** Pure state transitions for the favorites screen. */
object FavoritesReducer : Reducer<FavoritesState, FavoritesIntent> {

    override fun reduce(state: FavoritesState, intent: FavoritesIntent): FavoritesState = when (intent) {
        is FavoritesIntent.FavoritesLoaded -> state.copy(cities = intent.cities, isLoading = false)
        is FavoritesIntent.UnitLoaded -> state.copy(unit = intent.unit)

        // Optimistic: the row disappears at once; the database emission confirms it.
        is FavoritesIntent.RemoveClicked -> state.copy(cities = state.cities.filterNot { it.id == intent.city.id })

        FavoritesIntent.RefreshStarted -> state.copy(isRefreshing = true)
        FavoritesIntent.RefreshFinished -> state.copy(isRefreshing = false)

        is FavoritesIntent.CityClicked,
        is FavoritesIntent.UndoRemoveClicked,
        FavoritesIntent.SearchClicked,
        FavoritesIntent.RefreshRequested,
        -> state
    }
}
