package com.navyblue.navyweather.core.network

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.pluginOrNull
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

/** Bounded waiting and opt-in request logging (specs/networking). */
class HttpClientFactoryTest {

    private val okEngine = MockEngine { respond("{}") }

    @Test
    fun `default timeouts are 10 seconds to connect and 15 seconds per request`() {
        assertEquals(10_000, HttpClientFactory.CONNECT_TIMEOUT_MILLIS)
        assertEquals(15_000, HttpClientFactory.REQUEST_TIMEOUT_MILLIS)
    }

    @Test
    fun `request exceeding the timeout becomes a network error`() = runTest(timeout = 10.seconds) {
        val client = HttpClientFactory.create(
            engine = MockEngine {
                delay(5_000)
                respond("{}")
            },
            requestTimeoutMillis = 100,
        )

        val result = safeApiCall { client.get("https://test").body<String>() }

        assertEquals(AppResult.Failure(AppError.Network), result)
    }

    @Test
    fun `logging is not installed unless requested`() {
        val client = HttpClientFactory.create(okEngine)

        assertNull(client.pluginOrNull(Logging))
    }

    @Test
    fun `logging is installed when requested`() {
        val client = HttpClientFactory.create(okEngine, logLevel = LogLevel.INFO)

        assertNotNull(client.pluginOrNull(Logging))
    }
}
