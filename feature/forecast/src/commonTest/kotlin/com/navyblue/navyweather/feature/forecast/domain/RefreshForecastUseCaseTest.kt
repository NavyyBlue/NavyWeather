package com.navyblue.navyweather.feature.forecast.domain

import com.navyblue.navyweather.feature.forecast.FakeForecastRepository
import com.navyblue.navyweather.feature.forecast.FixedClock
import com.navyblue.navyweather.feature.forecast.domain.usecase.RefreshForecastUseCase
import com.navyblue.navyweather.feature.forecast.limaLocation
import com.navyblue.navyweather.feature.forecast.sampleForecast
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class RefreshForecastUseCaseTest {

    private val now = 1_000_000_000L

    @Test
    fun `fresh cache skips the network`() = runTest {
        val repository = FakeForecastRepository(initial = sampleForecast(updatedAt = now - 5.minutes.inWholeMilliseconds))

        RefreshForecastUseCase(repository, FixedClock(now))(limaLocation)

        assertEquals(0, repository.refreshCalls)
    }

    @Test
    fun `stale cache is refreshed`() = runTest {
        val repository = FakeForecastRepository(initial = sampleForecast(updatedAt = now - 11.minutes.inWholeMilliseconds))

        RefreshForecastUseCase(repository, FixedClock(now))(limaLocation)

        assertEquals(1, repository.refreshCalls)
    }

    @Test
    fun `no cache is refreshed`() = runTest {
        val repository = FakeForecastRepository(initial = null)

        RefreshForecastUseCase(repository, FixedClock(now))(limaLocation)

        assertEquals(1, repository.refreshCalls)
    }

    @Test
    fun `explicit refresh always fetches`() = runTest {
        val repository = FakeForecastRepository(initial = sampleForecast(updatedAt = now))

        RefreshForecastUseCase(repository, FixedClock(now))(limaLocation, force = true)

        assertEquals(1, repository.refreshCalls)
    }
}
