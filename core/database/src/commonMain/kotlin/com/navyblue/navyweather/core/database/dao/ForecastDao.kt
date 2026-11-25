package com.navyblue.navyweather.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query("SELECT * FROM current_weather WHERE cityId = :cityId")
    fun observeCurrent(cityId: Long): Flow<CurrentWeatherEntity?>

    @Query("SELECT * FROM daily_forecast WHERE cityId = :cityId ORDER BY date")
    fun observeDaily(cityId: Long): Flow<List<DailyForecastEntity>>

    /** Replaces the whole cached forecast of a city atomically, so observers never see a mix. */
    @Transaction
    suspend fun replaceForecast(current: CurrentWeatherEntity, daily: List<DailyForecastEntity>) {
        upsertCurrent(current)
        deleteDaily(current.cityId)
        upsertDaily(daily)
    }

    @Upsert
    suspend fun upsertCurrent(current: CurrentWeatherEntity)

    @Query("DELETE FROM daily_forecast WHERE cityId = :cityId")
    suspend fun deleteDaily(cityId: Long)

    // Upsert, not Insert: a repeated date in the API response must not abort the whole refresh.
    @Upsert
    suspend fun upsertDaily(daily: List<DailyForecastEntity>)
}
