package com.navyblue.navyweather.core.network

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Remote failures become typed errors (specs/error-handling) and responses tolerate new fields (specs/networking). */
class SafeApiCallTest {

    @Serializable
    private data class Greeting(val message: String)

    private fun clientRespondingWith(status: HttpStatusCode, body: String) = HttpClientFactory.create(
        MockEngine {
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        },
    )

    @Test
    fun `successful response is parsed and unknown fields are ignored`() = runTest {
        val client = clientRespondingWith(HttpStatusCode.OK, """{"message":"hi","extra":"ignored"}""")

        val result = safeApiCall { client.get("https://test").body<Greeting>() }

        assertEquals(AppResult.Success(Greeting("hi")), result)
    }

    @Test
    fun `non 2xx response becomes a server error with its status code`() = runTest {
        val client = clientRespondingWith(HttpStatusCode.ServiceUnavailable, "{}")

        val result = safeApiCall { client.get("https://test").body<Greeting>() }

        assertEquals(AppResult.Failure(AppError.Http(503)), result)
    }

    @Test
    fun `missing required field becomes a data error`() = runTest {
        val client = clientRespondingWith(HttpStatusCode.OK, """{"unexpected":true}""")

        val result = safeApiCall { client.get("https://test").body<Greeting>() }

        assertEquals(AppResult.Failure(AppError.Serialization), result)
    }

    @Test
    fun `invalid json becomes a data error`() = runTest {
        val client = clientRespondingWith(HttpStatusCode.OK, "<html>not json</html>")

        val result = safeApiCall { client.get("https://test").body<Greeting>() }

        assertEquals(AppResult.Failure(AppError.Serialization), result)
    }

    @Test
    fun `io failure becomes a network error`() = runTest {
        val client = HttpClientFactory.create(MockEngine { throw IOException("no route to host") })

        val result = safeApiCall { client.get("https://test").body<Greeting>() }

        assertEquals(AppResult.Failure(AppError.Network), result)
    }

    @Test
    fun `cancelled call stops without reporting an error`() = runTest {
        val requestStarted = CompletableDeferred<Unit>()
        val client = HttpClientFactory.create(
            MockEngine {
                requestStarted.complete(Unit)
                awaitCancellation()
            },
        )
        var result: AppResult<Greeting>? = null

        val job = launch { result = safeApiCall { client.get("https://test").body<Greeting>() } }
        requestStarted.await()
        job.cancel()
        job.join()

        assertTrue(job.isCancelled)
        assertNull(result)
    }
}
