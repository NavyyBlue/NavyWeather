package com.navyblue.navyweather.core.database.entity

import androidx.room.Entity

/** Cached daily forecast row. One row per city and day. */
@Entity(tableName = "daily_forecast", primaryKeys = ["cityId", "date"])
data class DailyForecastEntity(
    val cityId: Long,
    /** ISO-8601 local date (yyyy-MM-dd). */
    val date: String,
    val weatherCode: Int,
    val maxTemperatureCelsius: Double,
    val minTemperatureCelsius: Double,
)
