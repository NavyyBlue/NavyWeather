package com.navyblue.navyweather.feature.forecast.presentation

import androidx.lifecycle.viewModelScope
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.mvi.MviViewModel
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveFavoriteStatusUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveForecastUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveTemperatureUnitUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.RefreshForecastUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.SetTemperatureUnitUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ForecastViewModel(
    route: ForecastRoute,
    observeForecast: ObserveForecastUseCase,
    observeFavoriteStatus: ObserveFavoriteStatusUseCase,
    observeTemperatureUnit: ObserveTemperatureUnitUseCase,
    private val refreshForecast: RefreshForecastUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val setTemperatureUnit: SetTemperatureUnitUseCase,
) : MviViewModel<ForecastState, ForecastIntent, ForecastEffect>(
    initialState = ForecastState(location = route.toLocation()),
    reducer = ForecastReducer,
) {

    private var refreshJob: Job? = null
    private var forceRefreshPending = false

    init {
        val cityId = route.cityId
        observeForecast(cityId).onEach { onIntent(ForecastIntent.ForecastLoaded(it)) }.launchIn(viewModelScope)
        observeFavoriteStatus(cityId).onEach { onIntent(ForecastIntent.FavoriteStatusLoaded(it)) }.launchIn(viewModelScope)
        observeTemperatureUnit().onEach { onIntent(ForecastIntent.UnitLoaded(it)) }.launchIn(viewModelScope)
        refresh(force = false)
    }

    override fun handleIntent(intent: ForecastIntent) {
        when (intent) {
            ForecastIntent.RefreshRequested, ForecastIntent.RetryClicked -> refresh(force = true)

            // The reducer already flipped the state, so the value before the tap is the opposite.
            ForecastIntent.FavoriteToggled -> viewModelScope.launch {
                toggleFavorite(currentState.location, isFavorite = !currentState.isFavorite)
            }

            is ForecastIntent.UnitSelected -> viewModelScope.launch { setTemperatureUnit(intent.unit) }
            ForecastIntent.BackClicked -> sendEffect(ForecastEffect.NavigateBack)

            is ForecastIntent.ForecastLoaded,
            is ForecastIntent.FavoriteStatusLoaded,
            is ForecastIntent.UnitLoaded,
            ForecastIntent.RefreshStarted,
            ForecastIntent.RefreshSucceeded,
            is ForecastIntent.RefreshFailed,
            -> Unit
        }
    }

    /**
     * One refresh at a time. A forced refresh (pull or retry) that arrives while another is
     * running is queued and runs right after, so the user's explicit request is never dropped.
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
                onIntent(ForecastIntent.RefreshStarted)
                when (val result = refreshForecast(currentState.location, runForced)) {
                    is AppResult.Success -> onIntent(ForecastIntent.RefreshSucceeded)
                    is AppResult.Failure -> {
                        val hadData = currentState.forecast != null
                        onIntent(ForecastIntent.RefreshFailed(result.error))
                        if (hadData) sendEffect(ForecastEffect.ShowRefreshError(result.error))
                    }
                }
                runForced = forceRefreshPending
            } while (runForced)
        }
    }
}

private fun ForecastRoute.toLocation() = Location(
    id = cityId,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
)
