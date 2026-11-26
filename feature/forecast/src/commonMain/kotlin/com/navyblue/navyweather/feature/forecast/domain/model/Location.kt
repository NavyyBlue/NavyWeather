package com.navyblue.navyweather.feature.forecast.domain.model

/** The place a forecast is for. `id` is the Open-Meteo geocoding id. */
data class Location(
    val id: Long,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
)
