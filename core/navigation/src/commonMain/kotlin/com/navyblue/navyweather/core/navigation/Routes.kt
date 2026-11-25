package com.navyblue.navyweather.core.navigation

import kotlinx.serialization.Serializable

/*
 * Type-safe navigation contracts shared by all features.
 * Features only know these routes, never each other: the NavHost in :shared wires them.
 * Arguments are primitives so no feature has to expose its domain models.
 */

@Serializable
data object FavoritesRoute

@Serializable
data object SearchRoute

@Serializable
data class ForecastRoute(
    val cityId: Long,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
)
