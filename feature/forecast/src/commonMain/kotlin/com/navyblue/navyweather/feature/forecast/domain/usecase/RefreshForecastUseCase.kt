package com.navyblue.navyweather.feature.forecast.domain.usecase

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import com.navyblue.navyweather.feature.forecast.domain.repository.ForecastRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Business rule: a cached forecast younger than [FRESHNESS] is good enough, so opening a city
 * again does not hit the network. An explicit user refresh ([force]) always fetches.
 */
class RefreshForecastUseCase(
    private val repository: ForecastRepository,
    private val clock: Clock,
) {

    suspend operator fun invoke(location: Location, force: Boolean = false): AppResult<Unit> {
        if (!force) {
            val lastUpdated = repository.lastUpdatedAtEpochMillis(location.id)
            val age = lastUpdated?.let { clock.now().toEpochMilliseconds() - it }
            if (age != null && age in 0 until FRESHNESS.inWholeMilliseconds) return AppResult.Success(Unit)
        }
        return repository.refreshForecast(location)
    }

    companion object {
        val FRESHNESS = 10.minutes
    }
}
