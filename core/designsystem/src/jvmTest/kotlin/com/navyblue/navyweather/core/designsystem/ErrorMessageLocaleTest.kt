package com.navyblue.navyweather.core.designsystem

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.designsystem.component.messageResource
import kotlinx.coroutines.test.runTest
import org.jetbrains.compose.resources.getString
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Messages follow the device language (specs/error-handling). Compose Resources reads the
 * language from the JVM default locale, which only the JVM test host lets a test change.
 */
class ErrorMessageLocaleTest {

    private val originalLocale = Locale.getDefault()

    @AfterTest
    fun restoreLocale() = Locale.setDefault(originalLocale)

    private suspend fun messageIn(language: String, error: AppError): String {
        Locale.setDefault(Locale.forLanguageTag(language))
        return getString(error.messageResource())
    }

    @Test
    fun `network message in English`() = runTest {
        assertEquals("Check your connection and try again.", messageIn("en", AppError.Network))
    }

    @Test
    fun `network message in Spanish`() = runTest {
        assertEquals("Revisa tu conexión e inténtalo de nuevo.", messageIn("es", AppError.Network))
    }

    @Test
    fun `every error kind has its own message in both languages`() = runTest {
        for (language in listOf("en", "es")) {
            val messages = ALL_ERROR_KINDS.map { messageIn(language, it) }
            assertTrue(messages.all { it.isNotBlank() }, "blank message in $language")
            assertEquals(messages.size, messages.toSet().size, "duplicate message in $language")
        }
        assertNotEquals(messageIn("en", AppError.Serialization), messageIn("es", AppError.Serialization))
    }
}
