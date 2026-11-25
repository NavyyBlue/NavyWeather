package com.navyblue.navyweather.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.navyblue.navyweather.core.designsystem.theme.NavySeed
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** Colors are generated from a single brand seed (specs/design-system). */
@OptIn(ExperimentalTestApi::class)
class ThemeTest {

    private fun schemeOf(darkTheme: Boolean, dynamicColor: Boolean = false): ColorScheme {
        lateinit var scheme: ColorScheme
        runComposeUiTest {
            setContent {
                NavyWeatherTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                    scheme = MaterialTheme.colorScheme
                }
            }
        }
        return scheme
    }

    @Test
    fun `light primary container is the brand seed`() {
        assertEquals(NavySeed, schemeOf(darkTheme = false).primaryContainer)
    }

    @Test
    fun `dark scheme comes from the same seed with its own primary`() {
        val light = schemeOf(darkTheme = false)
        val dark = schemeOf(darkTheme = true)

        assertNotEquals(light.primary, dark.primary)
        assertNotEquals(light.surface, dark.surface)
    }

    @Test
    fun `dynamic color off Android keeps the brand scheme`() {
        // The test hosts (JVM and iOS) have no wallpaper colors. ColorScheme has no equals(),
        // but its toString() lists every role, so equal strings mean equal schemes.
        assertEquals(
            schemeOf(darkTheme = false).toString(),
            schemeOf(darkTheme = false, dynamicColor = true).toString(),
        )
    }
}
