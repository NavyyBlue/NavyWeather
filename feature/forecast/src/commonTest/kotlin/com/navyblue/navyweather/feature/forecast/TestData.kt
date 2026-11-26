package com.navyblue.navyweather.feature.forecast

import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.datastore.UserPreferencesRepository
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.forecast.domain.model.CurrentWeather
import com.navyblue.navyweather.feature.forecast.domain.model.DailyForecast
import com.navyblue.navyweather.feature.forecast.domain.model.Forecast
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import com.navyblue.navyweather.feature.forecast.domain.model.Temperature
import com.navyblue.navyweather.feature.forecast.domain.model.WeatherCondition
import com.navyblue.navyweather.feature.forecast.domain.repository.FavoriteStatusRepository
import com.navyblue.navyweather.feature.forecast.domain.repository.ForecastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

val limaRoute = ForecastRoute(cityId = 3936456, name = "Lima", country = "Peru", latitude = -12.04, longitude = -77.03)
val limaLocation = Location(3936456, "Lima", "Peru", -12.04, -77.03)

fun sampleForecast(temperature: Double = 22.0, updatedAt: Long = 0) = Forecast(
    current = CurrentWeather(
        temperature = Temperature(temperature),
        feelsLike = Temperature(temperature - 1),
        relativeHumidity = 70,
        windSpeedKmh = 12.0,
        condition = WeatherCondition.PARTLY_CLOUDY,
        observedAt = LocalDateTime.parse("2026-11-25T10:00"),
    ),
    daily = listOf(
        DailyForecast(LocalDate.parse("2026-11-25"), WeatherCondition.CLEAR, Temperature(16.0), Temperature(24.0)),
    ),
    updatedAtEpochMillis = updatedAt,
)

class FixedClock(var nowMillis: Long) : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(nowMillis)
}

class FakeForecastRepository(
    initial: Forecast? = null,
    var refreshResult: AppResult<Unit> = AppResult.Success(Unit),
    private val refreshedForecast: Forecast? = sampleForecast(),
    /** Simulated download time, so tests can overlap refreshes. */
    private val refreshDelayMillis: Long = 0,
) : ForecastRepository {
    val forecast = MutableStateFlow(initial)
    var refreshCalls = 0

    override fun observeForecast(cityId: Long): Flow<Forecast?> = forecast

    override suspend fun refreshForecast(location: Location): AppResult<Unit> {
        refreshCalls++
        if (refreshDelayMillis > 0) delay(refreshDelayMillis)
        if (refreshResult is AppResult.Success) forecast.value = refreshedForecast
        return refreshResult
    }

    override suspend fun lastUpdatedAtEpochMillis(cityId: Long): Long? = forecast.value?.updatedAtEpochMillis
}

class FakeFavoriteStatusRepository : FavoriteStatusRepository {
    private val favorites = MutableStateFlow(emptySet<Long>())
    override fun observeIsFavorite(cityId: Long): Flow<Boolean> = favorites.map { cityId in it }
    override suspend fun setFavorite(location: Location, favorite: Boolean) {
        favorites.value = if (favorite) favorites.value + location.id else favorites.value - location.id
    }
}

class FakeUserPreferences : UserPreferencesRepository {
    override val temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        temperatureUnit.value = unit
    }
}

/** A full week starting on the observed date, with [condition] for the current weather. */
fun weekForecast(temperature: Double = 21.6, condition: WeatherCondition = WeatherCondition.PARTLY_CLOUDY) = Forecast(
    current = CurrentWeather(
        temperature = Temperature(temperature),
        feelsLike = Temperature(temperature - 1),
        relativeHumidity = 70,
        windSpeedKmh = 12.0,
        condition = condition,
        observedAt = LocalDateTime.parse("2026-11-25T10:00"),
    ),
    daily = (0 until 7).map { i ->
        DailyForecast(
            date = LocalDate.parse("2026-11-25").plus(i, DateTimeUnit.DAY),
            condition = WeatherCondition.CLEAR,
            min = Temperature(16.0),
            max = Temperature(24.0),
        )
    },
    updatedAtEpochMillis = 0,
)
