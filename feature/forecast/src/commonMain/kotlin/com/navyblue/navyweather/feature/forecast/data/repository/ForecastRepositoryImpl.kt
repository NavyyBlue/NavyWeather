package com.navyblue.navyweather.feature.forecast.data.repository

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.database.dao.ForecastDao
import com.navyblue.navyweather.core.weather.ForecastSync
import com.navyblue.navyweather.feature.forecast.data.mapper.toDomainForecast
import com.navyblue.navyweather.feature.forecast.domain.model.Forecast
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import com.navyblue.navyweather.feature.forecast.domain.repository.ForecastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class ForecastRepositoryImpl(
    private val sync: ForecastSync,
    private val dao: ForecastDao,
) : ForecastRepository {

    override fun observeForecast(cityId: Long): Flow<Forecast?> =
        combine(dao.observeCurrent(cityId), dao.observeDaily(cityId)) { current, daily ->
            current?.let { toDomainForecast(it, daily) }
        }.distinctUntilChanged()

    override suspend fun refreshForecast(location: Location): AppResult<Unit> =
        sync.sync(location.id, location.latitude, location.longitude)

    override suspend fun lastUpdatedAtEpochMillis(cityId: Long): Long? = sync.lastSyncedAtEpochMillis(cityId)
}
