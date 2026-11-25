package com.navyblue.navyweather.core.datastore

import kotlinx.coroutines.flow.Flow

/** User settings. A plain interface so features depend on it, never on DataStore itself. */
interface UserPreferencesRepository {
    val temperatureUnit: Flow<TemperatureUnit>
    suspend fun setTemperatureUnit(unit: TemperatureUnit)
}
