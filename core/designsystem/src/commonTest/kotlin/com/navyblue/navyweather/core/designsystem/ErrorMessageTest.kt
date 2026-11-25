package com.navyblue.navyweather.core.designsystem

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.designsystem.component.messageResource
import kotlinx.coroutines.test.runTest
import org.jetbrains.compose.resources.getString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Each error kind has a message (specs/error-handling). Runs on the JVM and iOS in the host's
 * language; the English and Spanish texts are pinned in `ErrorMessageLocaleTest` (jvmTest).
 */
class ErrorMessageTest {

    @Test
    fun `every error kind has a message`() = runTest {
        for (error in ALL_ERROR_KINDS) {
            assertTrue(getString(error.messageResource()).isNotBlank(), "$error")
        }
    }

    @Test
    fun `server errors share one message regardless of the code`() {
        assertEquals(AppError.Http(500).messageResource(), AppError.Http(503).messageResource())
    }
}

internal val ALL_ERROR_KINDS = listOf(AppError.Network, AppError.Http(503), AppError.Serialization, AppError.Unknown("boom"))
