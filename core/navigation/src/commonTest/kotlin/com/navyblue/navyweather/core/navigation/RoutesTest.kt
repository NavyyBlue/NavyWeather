package com.navyblue.navyweather.core.navigation

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutesTest {

    @Test
    fun `forecast route survives serialization with all its arguments`() {
        val route = ForecastRoute(cityId = 3936456, name = "Lima", country = "Perú", latitude = -12.04, longitude = -77.03)

        val restored = Json.decodeFromString<ForecastRoute>(Json.encodeToString(route))

        assertEquals(route, restored)
    }

    @Test
    fun `forecast route without country keeps it null`() {
        val route = ForecastRoute(cityId = 1, name = "Atlantis", country = null, latitude = 0.0, longitude = 0.0)

        assertEquals(route, Json.decodeFromString<ForecastRoute>(Json.encodeToString(route)))
    }
}
