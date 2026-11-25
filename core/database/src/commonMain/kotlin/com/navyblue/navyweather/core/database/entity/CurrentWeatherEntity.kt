package com.navyblue.navyweather.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cached current conditions for a city (offline-first). Values are stored in metric units. */
@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey val cityId: Long,
    val temperatureCelsius: Double,
    val apparentTemperatureCelsius: Double,
    val relativeHumidity: Int,
    val windSpeedKmh: Double,
    val weatherCode: Int,
    /** ISO-8601 local date-time reported by the API. */
    val observedAt: String,
    val fetchedAtEpochMillis: Long,
)
