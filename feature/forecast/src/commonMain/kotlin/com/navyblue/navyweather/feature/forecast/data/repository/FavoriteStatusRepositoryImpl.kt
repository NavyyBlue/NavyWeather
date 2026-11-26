package com.navyblue.navyweather.feature.forecast.data.repository

import com.navyblue.navyweather.core.database.dao.FavoriteCityDao
import com.navyblue.navyweather.feature.forecast.data.mapper.toFavoriteEntity
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import com.navyblue.navyweather.feature.forecast.domain.repository.FavoriteStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

/** Writes the same favorites table that feature:favorites reads; the features never talk directly. */
internal class FavoriteStatusRepositoryImpl(
    private val dao: FavoriteCityDao,
    private val clock: Clock,
) : FavoriteStatusRepository {

    override fun observeIsFavorite(cityId: Long): Flow<Boolean> = dao.observeIsFavorite(cityId)

    override suspend fun setFavorite(location: Location, favorite: Boolean) {
        if (favorite) {
            dao.upsert(location.toFavoriteEntity(addedAtEpochMillis = clock.now().toEpochMilliseconds()))
        } else {
            dao.deleteById(location.id)
        }
    }
}
