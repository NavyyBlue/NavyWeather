package com.navyblue.navyweather.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.navyblue.navyweather.core.designsystem.component.ErrorState
import com.navyblue.navyweather.core.designsystem.component.LoadingState
import com.navyblue.navyweather.core.designsystem.resources.Res
import com.navyblue.navyweather.core.designsystem.resources.loading
import com.navyblue.navyweather.core.designsystem.resources.retry
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Shared loading and error states (specs/design-system). Expected texts are read from the
 * same string resources inside the composition, so the tests pass in any host locale.
 */
@OptIn(ExperimentalTestApi::class)
class StatesTest {

    @Test
    fun `tapping retry invokes the action once`() = runComposeUiTest {
        var retries = 0
        lateinit var retry: String
        setContent {
            retry = stringResource(Res.string.retry)
            NavyWeatherTheme { ErrorState(message = "Check your connection.", onRetry = { retries++ }) }
        }

        onNodeWithText(retry).performClick()

        assertEquals(1, retries)
    }

    @Test
    fun `retry is reachable by scrolling at 200 percent font in a short window`() = runComposeUiTest {
        lateinit var retry: String
        setContent {
            retry = stringResource(Res.string.retry)
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                NavyWeatherTheme {
                    Box(Modifier.size(width = 320.dp, height = 160.dp)) {
                        ErrorState(message = "The weather service is not responding. Try again in a moment.", onRetry = {})
                    }
                }
            }
        }

        onNodeWithText(retry).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `loading state is announced to screen readers`() = runComposeUiTest {
        lateinit var loading: String
        setContent {
            loading = stringResource(Res.string.loading)
            NavyWeatherTheme { LoadingState() }
        }

        onNodeWithContentDescription(loading).assertIsDisplayed()
    }
}
