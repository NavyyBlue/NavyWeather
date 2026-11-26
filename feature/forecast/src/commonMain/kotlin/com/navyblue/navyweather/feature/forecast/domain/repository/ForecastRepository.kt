package com.navyblue.navyweather.feature.forecast.domain.repository

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.forecast.domain.model.Forecast
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import kotlinx.coroutines.flow.Flow

/** Offline-first: the cache is the single source of truth; [refreshForecast] only updates it. */
interface ForecastRepository {
    /** Emits `null` until a forecast for the city has been cached. */
    fun observeForecast(cityId: Long): Flow<Forecast?>
    suspend fun refreshForecast(location: Location): AppResult<Unit>
    suspend fun lastUpdatedAtEpochMillis(cityId: Long): Long?
}
