package com.navyblue.navyweather.feature.forecast.domain.repository

import com.navyblue.navyweather.feature.forecast.domain.model.Location
import kotlinx.coroutines.flow.Flow

/** What the forecast screen needs to know about favorites, and nothing more. */
interface FavoriteStatusRepository {
    fun observeIsFavorite(cityId: Long): Flow<Boolean>
    suspend fun setFavorite(location: Location, favorite: Boolean)
}
