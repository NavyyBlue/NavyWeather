package com.navyblue.navyweather.feature.favorites.domain.repository

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteCity>>
    suspend fun removeFavorite(cityId: Long)
    suspend fun saveFavorite(city: FavoriteCity)

    /** Downloads the current weather of [city] into the cache; observers get the new temperature. */
    suspend fun refreshWeather(city: FavoriteCity): AppResult<Unit>
}
