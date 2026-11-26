package com.navyblue.navyweather.feature.favorites.fake

import kotlinx.coroutines.delay
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.datastore.UserPreferencesRepository
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.Instant

/** In-memory repository: behaves like the real one without Room. Preferred over mocks for state. */
class FakeFavoritesRepository(
    initial: List<FavoriteCity> = emptyList(),
    var refreshResult: AppResult<Unit> = AppResult.Success(Unit),
    /** Temperature written by [refreshWeather], like a fresh download would. */
    private val refreshedTemperature: Double = 20.0,
    /** Simulated download time, so tests can overlap refreshes. */
    private val refreshDelayMillis: Long = 0,
) : FavoritesRepository {

    private val cities = MutableStateFlow(initial)
    val refreshedIds = mutableListOf<Long>()

    override fun observeFavorites(): Flow<List<FavoriteCity>> = cities

    override suspend fun removeFavorite(cityId: Long) = cities.update { list -> list.filterNot { it.id == cityId } }

    override suspend fun saveFavorite(city: FavoriteCity) = cities.update { list -> list.filterNot { it.id == city.id } + city }

    override suspend fun refreshWeather(city: FavoriteCity): AppResult<Unit> {
        refreshedIds += city.id
        if (refreshDelayMillis > 0) delay(refreshDelayMillis)
        if (refreshResult is AppResult.Success) {
            cities.update { list -> list.map { if (it.id == city.id) it.copy(temperatureCelsius = refreshedTemperature) else it } }
        }
        return refreshResult
    }
}

class FakeUserPreferences : UserPreferencesRepository {
    override val temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        temperatureUnit.value = unit
    }
}

class FixedClock(private val nowMillis: Long) : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(nowMillis)
}

object TestCities {
    val lima = FavoriteCity(id = 3936456, name = "Lima", country = "Peru", latitude = -12.04, longitude = -77.03)
    val madrid = FavoriteCity(id = 3117735, name = "Madrid", country = "Spain", latitude = 40.42, longitude = -3.70)
    val cusco = FavoriteCity(id = 3941584, name = "Cusco", country = "Peru", latitude = -13.53, longitude = -71.97)
}
