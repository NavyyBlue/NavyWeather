package com.navyblue.navyweather.core.network

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Runs a remote operation (the call and anything done with its response, such as caching it)
 * and turns every failure into an [AppResult.Failure], so no Ktor, kotlinx.io or storage
 * exception ever reaches the domain layer. Coroutine cancellation is always rethrown.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    throw e // Never swallow coroutine cancellation.
} catch (e: ResponseException) {
    AppResult.Failure(AppError.Http(e.response.status.value))
} catch (e: IOException) {
    AppResult.Failure(AppError.Network)
} catch (e: SerializationException) {
    AppResult.Failure(AppError.Serialization)
} catch (e: ContentConvertException) {
    AppResult.Failure(AppError.Serialization)
} catch (e: Exception) {
    AppResult.Failure(AppError.Unknown(e.message))
}
