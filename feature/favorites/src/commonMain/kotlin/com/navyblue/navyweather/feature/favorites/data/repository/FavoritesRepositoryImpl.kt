package com.navyblue.navyweather.feature.favorites.data.repository

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.database.dao.FavoriteCityDao
import com.navyblue.navyweather.core.weather.ForecastSync
import com.navyblue.navyweather.feature.favorites.data.mapper.toDomain
import com.navyblue.navyweather.feature.favorites.data.mapper.toEntity
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

internal class FavoritesRepositoryImpl(
    private val dao: FavoriteCityDao,
    private val forecastSync: ForecastSync,
    private val clock: Clock,
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<FavoriteCity>> =
        dao.observeAllWithWeather().map { rows -> rows.map { it.toDomain() } }

    override suspend fun removeFavorite(cityId: Long) = dao.deleteById(cityId)

    override suspend fun saveFavorite(city: FavoriteCity) =
        dao.upsert(city.toEntity(addedAtEpochMillis = clock.now().toEpochMilliseconds()))

    override suspend fun refreshWeather(city: FavoriteCity): AppResult<Unit> =
        forecastSync.sync(city.id, city.latitude, city.longitude)
}
