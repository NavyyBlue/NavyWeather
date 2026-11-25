package com.navyblue.navyweather.feature.favorites.domain.usecase

import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveTemperatureUnitUseCase(private val preferences: UserPreferencesRepository) {
    operator fun invoke(): Flow<TemperatureUnit> = preferences.temperatureUnit
}
