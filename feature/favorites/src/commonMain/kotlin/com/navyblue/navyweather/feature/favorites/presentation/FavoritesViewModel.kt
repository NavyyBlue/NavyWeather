package com.navyblue.navyweather.feature.favorites.presentation

import androidx.lifecycle.viewModelScope
import com.navyblue.navyweather.core.mvi.MviViewModel
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveTemperatureUnitUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RefreshFavoritesWeatherUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RemoveFavoriteUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RestoreFavoriteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(
    observeFavorites: ObserveFavoritesUseCase,
    observeTemperatureUnit: ObserveTemperatureUnitUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val restoreFavorite: RestoreFavoriteUseCase,
    private val refreshWeather: RefreshFavoritesWeatherUseCase,
) : MviViewModel<FavoritesState, FavoritesIntent, FavoritesEffect>(
    initialState = FavoritesState(),
    reducer = FavoritesReducer,
) {

    private var refreshJob: Job? = null
    private var refreshedOnOpen = false
    private var forceRefreshPending = false

    init {
        observeFavorites()
            .onEach { cities ->
                onIntent(FavoritesIntent.FavoritesLoaded(cities))
                // Once per screen: bring stale temperatures up to date with the first real list.
                if (!refreshedOnOpen && cities.isNotEmpty()) {
                    refreshedOnOpen = true
                    refresh(force = false)
                }
            }
            .launchIn(viewModelScope)
        observeTemperatureUnit().onEach { onIntent(FavoritesIntent.UnitLoaded(it)) }.launchIn(viewModelScope)
    }

    override fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.CityClicked -> sendEffect(FavoritesEffect.NavigateToForecast(intent.city.toRoute()))

            is FavoritesIntent.RemoveClicked -> viewModelScope.launch {
                val removed = removeFavorite(intent.city)
                sendEffect(FavoritesEffect.ShowRemoved(removed))
            }

            is FavoritesIntent.UndoRemoveClicked -> viewModelScope.launch { restoreFavorite(intent.city) }

            FavoritesIntent.SearchClicked -> sendEffect(FavoritesEffect.NavigateToSearch)
            FavoritesIntent.RefreshRequested -> refresh(force = true)

            is FavoritesIntent.FavoritesLoaded,
            is FavoritesIntent.UnitLoaded,
            FavoritesIntent.RefreshStarted,
            FavoritesIntent.RefreshFinished,
            -> Unit // State only, handled by the reducer.
        }
    }

    /**
     * One refresh at a time. A pull to refresh that arrives while a background refresh is running
     * is not dropped: it runs right after, so the user's explicit request is always honored.
     */
    private fun refresh(force: Boolean) {
        if (refreshJob?.isActive == true) {
            if (force) forceRefreshPending = true
            return
        }
        refreshJob = viewModelScope.launch {
            var runForced = force
            do {
                forceRefreshPending = false
                onIntent(FavoritesIntent.RefreshStarted)
                val error = refreshWeather(currentState.cities, runForced)
                onIntent(FavoritesIntent.RefreshFinished)
                // Background refreshes fail silently (cached values stay); a pull deserves an answer.
                if (error != null && runForced) sendEffect(FavoritesEffect.ShowRefreshError(error))
                runForced = forceRefreshPending
            } while (runForced)
        }
    }

    private fun FavoriteCity.toRoute() = ForecastRoute(
        cityId = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
    )
}
