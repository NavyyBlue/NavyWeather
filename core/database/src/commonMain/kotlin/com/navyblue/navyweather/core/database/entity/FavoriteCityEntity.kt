package com.navyblue.navyweather.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A city the user marked as favorite. `id` is the Open-Meteo geocoding id. */
@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val addedAtEpochMillis: Long,
)
