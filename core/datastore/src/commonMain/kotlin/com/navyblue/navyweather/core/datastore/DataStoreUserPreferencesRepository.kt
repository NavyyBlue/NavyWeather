package com.navyblue.navyweather.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import okio.IOException

internal class DataStoreUserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val temperatureUnit: Flow<TemperatureUnit> = dataStore.data
        // An unreadable file must not break the screens observing settings: use the defaults.
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { preferences ->
            preferences[TEMPERATURE_UNIT]
                ?.let { stored -> TemperatureUnit.entries.firstOrNull { it.name == stored } }
                ?: TemperatureUnit.CELSIUS
        }
        .distinctUntilChanged()

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { it[TEMPERATURE_UNIT] = unit.name }
    }

    private companion object {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
    }
}
