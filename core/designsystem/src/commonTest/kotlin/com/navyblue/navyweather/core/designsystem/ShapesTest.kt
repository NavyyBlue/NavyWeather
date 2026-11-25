package com.navyblue.navyweather.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** The theme exposes the Expressive corner scale (specs/design-system). */
@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3ExpressiveApi::class)
class ShapesTest {

    @Test
    fun `theme uses the 4 to 48 dp corner scale`() = runComposeUiTest {
        lateinit var shapes: Shapes
        setContent { NavyWeatherTheme { shapes = MaterialTheme.shapes } }

        assertEquals(
            listOf(4, 8, 12, 16, 20, 28, 32, 48).map { RoundedCornerShape(it.dp) },
            listOf(
                shapes.extraSmall,
                shapes.small,
                shapes.medium,
                shapes.large,
                shapes.largeIncreased,
                shapes.extraLarge,
                shapes.extraLargeIncreased,
                shapes.extraExtraLarge,
            ),
        )
    }
}
