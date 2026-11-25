package com.navyblue.navyweather.feature.favorites.domain.usecase

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Keeps the temperatures on the favorites list current without wasting requests:
 * only places whose cached weather is missing or older than [FRESHNESS] are downloaded
 * (all of them when the user pulls to refresh), in parallel.
 *
 * @return the first error if any download failed (cached values stay on screen), or `null`.
 */
class RefreshFavoritesWeatherUseCase(
    private val repository: FavoritesRepository,
    private val clock: Clock,
) {

    suspend operator fun invoke(cities: List<FavoriteCity>, force: Boolean = false): AppError? {
        val now = clock.now().toEpochMilliseconds()
        val toRefresh = if (force) cities else cities.filter { it.isStale(now) }
        val results = coroutineScope {
            toRefresh.map { city -> async { repository.refreshWeather(city) } }.awaitAll()
        }
        return results.filterIsInstance<AppResult.Failure>().firstOrNull()?.error
    }

    private fun FavoriteCity.isStale(now: Long): Boolean {
        val updatedAt = weatherUpdatedAtEpochMillis ?: return true
        return now - updatedAt !in 0 until FRESHNESS.inWholeMilliseconds
    }

    companion object {
        val FRESHNESS = 10.minutes
    }
}
