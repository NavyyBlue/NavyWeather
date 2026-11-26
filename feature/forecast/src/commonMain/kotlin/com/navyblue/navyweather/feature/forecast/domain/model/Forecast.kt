package com.navyblue.navyweather.feature.forecast.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Forecast(
    val current: CurrentWeather,
    val daily: List<DailyForecast>,
    /** When this data was downloaded (device clock), to tell how fresh the cache is. */
    val updatedAtEpochMillis: Long,
)

data class CurrentWeather(
    val temperature: Temperature,
    val feelsLike: Temperature,
    val relativeHumidity: Int,
    val windSpeedKmh: Double,
    val condition: WeatherCondition,
    /** Local time at the location. */
    val observedAt: LocalDateTime,
)

data class DailyForecast(
    val date: LocalDate,
    val condition: WeatherCondition,
    val min: Temperature,
    val max: Temperature,
)
