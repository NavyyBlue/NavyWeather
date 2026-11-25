package com.navyblue.navyweather.core.weather

import com.navyblue.navyweather.core.network.HttpClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** A forecast is downloaded for a place's coordinates (specs/weather-data). */
class OpenMeteoForecastApiTest {

    private var request: HttpRequestData? = null

    private val api = OpenMeteoForecastApi(
        HttpClientFactory.create(
            MockEngine { data ->
                request = data
                respond(forecastJson(), headers = headersOf(HttpHeaders.ContentType, "application/json"))
            },
        ),
    )

    @Test
    fun `request asks for the coordinates and fields over 7 days in local time`() = runTest {
        api.forecast(latitude = -12.05, longitude = -77.04)

        val url = requireNotNull(request).url
        assertEquals("https://api.open-meteo.com/v1/forecast", "${url.protocol.name}://${url.host}${url.encodedPath}")
        assertEquals("-12.05", url.parameters["latitude"])
        assertEquals("-77.04", url.parameters["longitude"])
        assertEquals(
            "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m",
            url.parameters["current"],
        )
        assertEquals("weather_code,temperature_2m_max,temperature_2m_min", url.parameters["daily"])
        assertEquals("7", url.parameters["forecast_days"])
        assertEquals("auto", url.parameters["timezone"])
        assertNull(url.parameters["apikey"]) // No API key is needed.
    }

    @Test
    fun `response is parsed into current conditions and daily lists`() = runTest {
        val response = api.forecast(latitude = -12.05, longitude = -77.04)

        assertEquals(21.4, response.current.temperature)
        assertEquals(74, response.current.relativeHumidity)
        assertEquals(7, response.daily.time.size)
        assertEquals("2026-11-25", response.daily.time.first())
    }
}
