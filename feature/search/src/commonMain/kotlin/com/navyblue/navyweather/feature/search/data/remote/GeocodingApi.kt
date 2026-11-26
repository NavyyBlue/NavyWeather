package com.navyblue.navyweather.feature.search.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Open-Meteo Geocoding API (free, no API key). */
internal class GeocodingApi(private val client: HttpClient) {

    suspend fun search(name: String, languageCode: String): GeocodingResponseDto =
        client.get(BASE_URL) {
            parameter("name", name)
            parameter("count", RESULT_COUNT)
            parameter("language", languageCode)
            parameter("format", "json")
        }.body()

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/search"
        const val RESULT_COUNT = 10
    }
}

/** `results` is absent (not empty) when nothing matches. */
@Serializable
internal data class GeocodingResponseDto(
    val results: List<GeocodingResultDto> = emptyList(),
)

@Serializable
internal data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @SerialName("admin1") val region: String? = null,
)
