package com.navyblue.navyweather.feature.forecast.presentation

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.feature.forecast.limaLocation
import com.navyblue.navyweather.feature.forecast.sampleForecast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ForecastReducerTest {

    private val empty = ForecastState(location = limaLocation)

    @Test
    fun `starts in the initial loading state`() {
        assertTrue(empty.isInitialLoading)
    }

    @Test
    fun `refresh failure without cache shows a full-screen error`() {
        val state = ForecastReducer.reduce(empty.copy(isRefreshing = true), ForecastIntent.RefreshFailed(AppError.Network))

        assertEquals(AppError.Network, state.error)
        assertFalse(state.isRefreshing)
        assertFalse(state.isInitialLoading)
    }

    @Test
    fun `refresh failure with cache keeps showing the data`() {
        val cached = empty.copy(forecast = sampleForecast(), isRefreshing = true)

        val state = ForecastReducer.reduce(cached, ForecastIntent.RefreshFailed(AppError.Network))

        assertNull(state.error)
        assertEquals(cached.forecast, state.forecast)
    }

    @Test
    fun `retry clears the error so the loading state shows again`() {
        val failed = empty.copy(error = AppError.Network)

        val state = ForecastReducer.reduce(failed, ForecastIntent.RefreshStarted)

        assertTrue(state.isInitialLoading)
    }

    @Test
    fun `favorite flips optimistically`() {
        assertTrue(ForecastReducer.reduce(empty, ForecastIntent.FavoriteToggled).isFavorite)
    }
}
