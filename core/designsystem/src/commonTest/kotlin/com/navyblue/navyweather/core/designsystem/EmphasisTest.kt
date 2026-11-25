package com.navyblue.navyweather.core.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.navyblue.navyweather.core.designsystem.component.EmptyState
import com.navyblue.navyweather.core.designsystem.component.ErrorState
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Emphasis is spent deliberately (specs/design-system): shared states sit on surface roles and
 * never use the strong primaryContainer fill, which is reserved for a screen's hero.
 */
@OptIn(ExperimentalTestApi::class)
class EmphasisTest {

    private fun colorsOf(content: @Composable () -> Unit): Pair<ColorScheme, Set<Color>> {
        lateinit var scheme: ColorScheme
        lateinit var colors: Set<Color>
        runComposeUiTest {
            setContent {
                NavyWeatherTheme(darkTheme = false) {
                    scheme = MaterialTheme.colorScheme
                    Surface(Modifier.size(360.dp, 640.dp).testTag(TAG)) { content() }
                }
            }
            val pixels = onNodeWithTag(TAG).captureToImage().toPixelMap()
            colors = buildSet {
                for (x in 0 until pixels.width) for (y in 0 until pixels.height) add(pixels[x, y])
            }
        }
        return scheme to colors
    }

    @Test
    fun `error state uses surface and primary for its action but no primary container`() {
        val (scheme, colors) = colorsOf { ErrorState(message = "Check your connection.", onRetry = {}) }

        assertTrue(scheme.surface in colors, "the state sits on surface")
        assertTrue(scheme.primary in colors, "the retry button is the primary action")
        assertFalse(scheme.primaryContainer in colors, "primaryContainer is reserved for heroes")
    }

    @Test
    fun `empty state uses no primary container`() {
        val (scheme, colors) = colorsOf {
            EmptyState(
                icon = Icons.Rounded.Search,
                title = "No favorites yet",
                message = "Search for a city to add it.",
                action = { Button(onClick = {}) { Text("Search") } },
            )
        }

        assertTrue(scheme.surface in colors)
        assertFalse(scheme.primaryContainer in colors)
    }

    private companion object {
        const val TAG = "state"
    }
}
