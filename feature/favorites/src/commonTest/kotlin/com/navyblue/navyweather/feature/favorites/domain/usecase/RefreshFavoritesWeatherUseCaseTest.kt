package com.navyblue.navyweather.feature.favorites.domain.usecase

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.favorites.fake.FakeFavoritesRepository
import com.navyblue.navyweather.feature.favorites.fake.FixedClock
import com.navyblue.navyweather.feature.favorites.fake.TestCities
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class RefreshFavoritesWeatherUseCaseTest {

    private val now = 1_000_000_000L
    private val fresh = TestCities.lima.copy(temperatureCelsius = 20.0, weatherUpdatedAtEpochMillis = now - 2.minutes.inWholeMilliseconds)
    private val stale = TestCities.madrid.copy(temperatureCelsius = 10.0, weatherUpdatedAtEpochMillis = now - 30.minutes.inWholeMilliseconds)
    private val missing = TestCities.cusco

    @Test
    fun `only stale or missing temperatures are downloaded`() = runTest {
        val repository = FakeFavoritesRepository()

        val error = RefreshFavoritesWeatherUseCase(repository, FixedClock(now))(listOf(fresh, stale, missing))

        assertEquals(setOf(stale.id, missing.id), repository.refreshedIds.toSet())
        assertNull(error)
    }

    @Test
    fun `pull to refresh downloads every place`() = runTest {
        val repository = FakeFavoritesRepository()

        RefreshFavoritesWeatherUseCase(repository, FixedClock(now))(listOf(fresh, stale), force = true)

        assertEquals(setOf(fresh.id, stale.id), repository.refreshedIds.toSet())
    }

    @Test
    fun `failures are reported without throwing`() = runTest {
        val repository = FakeFavoritesRepository(refreshResult = AppResult.Failure(AppError.Network))

        val error = RefreshFavoritesWeatherUseCase(repository, FixedClock(now))(listOf(missing))

        assertEquals(AppError.Network, error)
    }
}
