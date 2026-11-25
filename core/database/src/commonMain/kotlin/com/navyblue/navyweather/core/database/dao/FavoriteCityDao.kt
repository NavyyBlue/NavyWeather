package com.navyblue.navyweather.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import com.navyblue.navyweather.core.database.entity.FavoriteCityWithWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {

    /** Most recently added first. Emits again on every change. */
    @Query("SELECT * FROM favorite_cities ORDER BY addedAtEpochMillis DESC")
    fun observeAll(): Flow<List<FavoriteCityEntity>>

    /** Favorites with their cached temperature; emits again when either table changes. */
    @Query(
        """
        SELECT f.*, c.temperatureCelsius AS temperatureCelsius, c.fetchedAtEpochMillis AS weatherFetchedAtEpochMillis
        FROM favorite_cities f
        LEFT JOIN current_weather c ON c.cityId = f.id
        ORDER BY f.addedAtEpochMillis DESC
        """,
    )
    fun observeAllWithWeather(): Flow<List<FavoriteCityWithWeather>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = :id)")
    fun observeIsFavorite(id: Long): Flow<Boolean>

    @Upsert
    suspend fun upsert(city: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE id = :id")
    suspend fun deleteById(id: Long)
}
