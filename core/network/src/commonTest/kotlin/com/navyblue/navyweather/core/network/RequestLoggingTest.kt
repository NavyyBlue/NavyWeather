package com.navyblue.navyweather.core.network

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.platformLogWriter
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/** Request logging is off unless enabled, and goes to the debug log when it is (specs/networking). */
class RequestLoggingTest {

    private val logged = mutableListOf<String>()

    private val capturingWriter = object : LogWriter() {
        override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
            if (tag == "HTTP") logged += "$severity $message"
        }
    }

    @BeforeTest
    fun captureLogs() = Logger.setLogWriters(capturingWriter)

    @AfterTest
    fun restoreLogs() = Logger.setLogWriters(platformLogWriter())

    private suspend fun requestWith(logLevel: LogLevel) {
        val client = HttpClientFactory.create(MockEngine { respond("{}") }, logLevel = logLevel)
        client.get("https://api.example.com/v1/forecast?latitude=-12.05")
    }

    @Test
    fun `nothing is logged when logging is not requested`() = runTest {
        requestWith(LogLevel.NONE)

        assertTrue(logged.isEmpty(), "$logged")
    }

    @Test
    fun `request line and response status go to the debug log when requested`() = runTest {
        requestWith(LogLevel.INFO)

        assertTrue(logged.any { it.startsWith("Debug") && "https://api.example.com/v1/forecast?latitude=-12.05" in it }, "$logged")
        assertTrue(logged.any { "200" in it }, "$logged")
    }
}
