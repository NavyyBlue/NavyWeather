package com.navyblue.navyweather.feature.favorites.domain.model

/** A place the user saved, with its last known temperature. Pure Kotlin: no Room, Ktor or Compose types. */
data class FavoriteCity(
    val id: Long,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    /** Current temperature from the cache, `null` until it has been downloaded once. */
    val temperatureCelsius: Double? = null,
    val weatherUpdatedAtEpochMillis: Long? = null,
)
