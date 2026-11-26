package com.navyblue.navyweather.feature.forecast.domain.usecase

import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.datastore.UserPreferencesRepository
import com.navyblue.navyweather.feature.forecast.domain.model.Forecast
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import com.navyblue.navyweather.feature.forecast.domain.repository.FavoriteStatusRepository
import com.navyblue.navyweather.feature.forecast.domain.repository.ForecastRepository
import kotlinx.coroutines.flow.Flow

class ObserveForecastUseCase(private val repository: ForecastRepository) {
    operator fun invoke(cityId: Long): Flow<Forecast?> = repository.observeForecast(cityId)
}

class ObserveFavoriteStatusUseCase(private val repository: FavoriteStatusRepository) {
    operator fun invoke(cityId: Long): Flow<Boolean> = repository.observeIsFavorite(cityId)
}

/** Flips the favorite state of [location] based on what the user currently sees. */
class ToggleFavoriteUseCase(private val repository: FavoriteStatusRepository) {
    suspend operator fun invoke(location: Location, isFavorite: Boolean) =
        repository.setFavorite(location, favorite = !isFavorite)
}

class ObserveTemperatureUnitUseCase(private val preferences: UserPreferencesRepository) {
    operator fun invoke(): Flow<TemperatureUnit> = preferences.temperatureUnit
}

class SetTemperatureUnitUseCase(private val preferences: UserPreferencesRepository) {
    suspend operator fun invoke(unit: TemperatureUnit) = preferences.setTemperatureUnit(unit)
}
