package com.navyblue.navyweather.feature.search.data

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.network.HttpClientFactory
import com.navyblue.navyweather.feature.search.data.remote.GeocodingApi
import com.navyblue.navyweather.feature.search.data.repository.SearchRepositoryImpl
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Repository + API against Ktor's MockEngine: real JSON parsing, no network. */
class GeocodingApiTest {

    private var requestedUrl: String? = null

    private fun repository(status: HttpStatusCode, body: String) = SearchRepositoryImpl(
        GeocodingApi(
            HttpClientFactory.create(
                MockEngine { request ->
                    requestedUrl = request.url.toString()
                    respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
                },
            ),
        ),
    )

    @Test
    fun `parses results and sends name count and language`() = runTest {
        val json = """
            {"results":[{"id":3936456,"name":"Lima","latitude":-12.04,"longitude":-77.03,
            "country":"Perú","admin1":"Lima","timezone":"America/Lima"}],"generationtime_ms":0.5}
        """.trimIndent()

        val result = repository(HttpStatusCode.OK, json).searchCities("Lima", "es")

        assertEquals(
            AppResult.Success(listOf(CitySearchResult(3936456, "Lima", "Lima", "Perú", -12.04, -77.03))),
            result,
        )
        val url = requireNotNull(requestedUrl)
        listOf("name=Lima", "count=10", "language=es", "format=json").forEach { assertTrue(it in url, "$it missing in $url") }
    }

    @Test
    fun `missing results field means no matches`() = runTest {
        val result = repository(HttpStatusCode.OK, """{"generationtime_ms":0.3}""").searchCities("Xyzzy", "en")

        assertEquals(AppResult.Success(emptyList()), result)
    }

    @Test
    fun `server errors become AppError Http`() = runTest {
        val result = repository(HttpStatusCode.InternalServerError, "{}").searchCities("Lima", "en")

        assertEquals(AppResult.Failure(AppError.Http(500)), result)
    }
}
