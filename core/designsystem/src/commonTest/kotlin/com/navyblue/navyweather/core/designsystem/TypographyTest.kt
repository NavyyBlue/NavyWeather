package com.navyblue.navyweather.core.designsystem

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.font.FontFamily
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.core.designsystem.theme.googleSansFlexFamily
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Every type role uses Google Sans Flex on both platforms (specs/design-system). */
@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3ExpressiveApi::class)
class TypographyTest {

    @Test
    fun everyRoleUsesGoogleSansFlex() = runComposeUiTest {
        lateinit var typography: Typography
        lateinit var family: FontFamily
        setContent {
            family = googleSansFlexFamily()
            NavyWeatherTheme { typography = MaterialTheme.typography }
        }

        val roles = with(typography) {
            mapOf(
                "displayLarge" to displayLarge, "displayMedium" to displayMedium, "displaySmall" to displaySmall,
                "headlineLarge" to headlineLarge, "headlineMedium" to headlineMedium, "headlineSmall" to headlineSmall,
                "titleLarge" to titleLarge, "titleMedium" to titleMedium, "titleSmall" to titleSmall,
                "bodyLarge" to bodyLarge, "bodyMedium" to bodyMedium, "bodySmall" to bodySmall,
                "labelLarge" to labelLarge, "labelMedium" to labelMedium, "labelSmall" to labelSmall,
                "displayLargeEmphasized" to displayLargeEmphasized, "titleLargeEmphasized" to titleLargeEmphasized,
                "bodyLargeEmphasized" to bodyLargeEmphasized, "labelLargeEmphasized" to labelLargeEmphasized,
            )
        }
        assertTrue(roles.size >= 19)
        roles.forEach { (name, style) -> assertEquals(family, style.fontFamily, "$name uses Google Sans Flex") }
    }
}
