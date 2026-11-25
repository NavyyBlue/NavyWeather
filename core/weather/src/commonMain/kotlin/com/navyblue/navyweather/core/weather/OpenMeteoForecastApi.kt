package com.navyblue.navyweather.core.weather

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Open-Meteo Forecast API (free, no API key). Metric units; the app converts for display. */
class OpenMeteoForecastApi(private val client: HttpClient) {

    suspend fun forecast(latitude: Double, longitude: Double): ForecastResponseDto =
        client.get(BASE_URL) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("current", CURRENT_FIELDS)
            parameter("daily", DAILY_FIELDS)
            parameter("timezone", "auto") // Dates and times in the location's own time zone.
            parameter("forecast_days", FORECAST_DAYS)
        }.body()

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/forecast"
        const val CURRENT_FIELDS = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m"
        const val DAILY_FIELDS = "weather_code,temperature_2m_max,temperature_2m_min"
        const val FORECAST_DAYS = 7
    }
}

@Serializable
data class ForecastResponseDto(
    val current: CurrentDto,
    val daily: DailyDto,
)

@Serializable
data class CurrentDto(
    val time: String,
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("relative_humidity_2m") val relativeHumidity: Int,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("wind_speed_10m") val windSpeed: Double,
)

/** Open-Meteo returns daily data as parallel arrays, one entry per day. */
@Serializable
data class DailyDto(
    val time: List<String>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double>,
)
