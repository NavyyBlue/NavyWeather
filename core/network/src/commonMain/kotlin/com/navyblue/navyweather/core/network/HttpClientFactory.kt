package com.navyblue.navyweather.core.network

import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** JSON settings shared by every API: tolerant to new fields added by the server. */
val NetworkJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

object HttpClientFactory {

    const val CONNECT_TIMEOUT_MILLIS: Long = 10_000
    const val REQUEST_TIMEOUT_MILLIS: Long = 15_000

    /**
     * Builds the single [HttpClient] of the app. The engine is injected so production uses the
     * native one per platform ([platformHttpEngine]) and tests use Ktor's `MockEngine`.
     *
     * @param logLevel HTTP logging. Off by default; the app enables it only in debug builds,
     * so request URLs never reach release logs.
     * @param connectTimeoutMillis time allowed to establish the connection.
     * @param requestTimeoutMillis time allowed for the whole request; exceeding it is a network error.
     */
    fun create(
        engine: HttpClientEngine,
        logLevel: LogLevel = LogLevel.NONE,
        connectTimeoutMillis: Long = CONNECT_TIMEOUT_MILLIS,
        requestTimeoutMillis: Long = REQUEST_TIMEOUT_MILLIS,
    ): HttpClient = HttpClient(engine) {
        // Non-2xx responses throw ResponseException, which safeApiCall maps to AppError.Http.
        expectSuccess = true

        install(ContentNegotiation) { json(NetworkJson) }

        install(HttpTimeout) {
            this.connectTimeoutMillis = connectTimeoutMillis
            this.requestTimeoutMillis = requestTimeoutMillis
            // The Darwin engine ignores the connect timeout and only applies the socket timeout
            // (as NSURLRequest.timeoutInterval). Using the same value keeps a stalled connection
            // bounded by 10 s on iOS too; on OkHttp it bounds reads and writes the same way.
            socketTimeoutMillis = connectTimeoutMillis
        }

        if (logLevel != LogLevel.NONE) {
            install(Logging) {
                level = logLevel
                val httpLogger = KermitLogger.withTag("HTTP")
                logger = object : Logger {
                    override fun log(message: String) = httpLogger.d { message }
                }
            }
        }
    }
}

/** Native engine per platform: OkHttp on Android/JVM, Darwin (NSURLSession) on iOS. */
expect fun platformHttpEngine(): HttpClientEngine
