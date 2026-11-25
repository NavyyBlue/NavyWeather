package com.navyblue.navyweather.core.common.locale

/** Device language as an ISO 639-1 code ("en", "es"). An interface so tests can fix it. */
fun interface LanguageProvider {
    fun languageCode(): String
}

/** Reads the language the user set on the device. */
object SystemLanguageProvider : LanguageProvider {
    override fun languageCode(): String = platformLanguageCode()
}

internal expect fun platformLanguageCode(): String
