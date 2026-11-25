package com.navyblue.navyweather.core.common.result

/** Errors the app knows how to explain to the user. Independent of Ktor, Room or any platform. */
sealed interface AppError {
    /** No connectivity, timeout or DNS failure. */
    data object Network : AppError

    /** The server answered with a non-2xx status. */
    data class Http(val code: Int) : AppError

    /** The response could not be parsed. */
    data object Serialization : AppError

    data class Unknown(val message: String? = null) : AppError
}
