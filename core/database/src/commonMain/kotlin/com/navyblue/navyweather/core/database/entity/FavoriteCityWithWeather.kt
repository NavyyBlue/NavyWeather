package com.navyblue.navyweather.core.database.entity

import androidx.room.Embedded

/** A favorite plus its cached current temperature, if any (LEFT JOIN on current_weather). */
data class FavoriteCityWithWeather(
    @Embedded val city: FavoriteCityEntity,
    val temperatureCelsius: Double?,
    val weatherFetchedAtEpochMillis: Long?,
)
