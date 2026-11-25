package com.navyblue.navyweather.core.weather

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.common.result.map
import com.navyblue.navyweather.core.database.dao.ForecastDao
import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import com.navyblue.navyweather.core.network.safeApiCall
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

/**
 * Downloads a forecast from Open-Meteo and writes it into the Room cache.
 *
 * Shared by feature:forecast (one city) and feature:favorites (the temperature of every saved
 * place), which is why it lives in core: two features need it and they must not depend on
 * each other. Freshness rules stay in each feature's domain layer.
 */
class ForecastSync(
    private val api: OpenMeteoForecastApi,
    private val dao: ForecastDao,
    private val clock: Clock,
) {

    suspend fun sync(cityId: Long, latitude: Double, longitude: Double): AppResult<Unit> =
        safeApiCall { api.forecast(latitude, longitude) }.map { response ->
            dao.replaceForecast(
                current = response.toCurrentEntity(cityId, clock.now().toEpochMilliseconds()),
                daily = response.toDailyEntities(cityId),
            )
        }

    suspend fun lastSyncedAtEpochMillis(cityId: Long): Long? =
        dao.observeCurrent(cityId).first()?.fetchedAtEpochMillis
}

internal fun ForecastResponseDto.toCurrentEntity(cityId: Long, fetchedAtEpochMillis: Long) = CurrentWeatherEntity(
    cityId = cityId,
    temperatureCelsius = current.temperature,
    apparentTemperatureCelsius = current.apparentTemperature,
    relativeHumidity = current.relativeHumidity,
    windSpeedKmh = current.windSpeed,
    weatherCode = current.weatherCode,
    observedAt = current.time,
    fetchedAtEpochMillis = fetchedAtEpochMillis,
)

/** Zips the parallel daily arrays; a malformed response with uneven arrays keeps the common days. */
internal fun ForecastResponseDto.toDailyEntities(cityId: Long): List<DailyForecastEntity> {
    val days = minOf(daily.time.size, daily.weatherCode.size, daily.temperatureMax.size, daily.temperatureMin.size)
    return (0 until days).map { i ->
        DailyForecastEntity(
            cityId = cityId,
            date = daily.time[i],
            weatherCode = daily.weatherCode[i],
            maxTemperatureCelsius = daily.temperatureMax[i],
            minTemperatureCelsius = daily.temperatureMin[i],
        )
    }
}
