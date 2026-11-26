package com.navyblue.navyweather.core.designsystem.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.navyblue.navyweather.core.designsystem.resources.Res
import com.navyblue.navyweather.core.designsystem.resources.google_sans_flex
import org.jetbrains.compose.resources.Font

/** Weights the app uses; each one is an instance of the variable font's `wght` axis. */
private val FontWeights = listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold)

/**
 * Google Sans Flex (ADR-0018), bundled as a Compose resource with its license under
 * `files/licenses/google-sans-flex`. One variable file serves every weight on Android and iOS.
 */
@Composable
fun googleSansFlexFamily(): FontFamily {
    val fonts = FontWeights.map { weight ->
        Font(
            resource = Res.font.google_sans_flex,
            weight = weight,
            variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
        )
    }
    return remember(fonts) { FontFamily(fonts) }
}

/**
 * Material 3 role scale (Display, Headline, Title, Body, Label), including the Expressive
 * emphasized variants, set in Google Sans Flex.
 * Screens use the roles, never raw font sizes or families.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun navyTypography(): Typography {
    val family = googleSansFlexFamily()
    return remember(family) {
        val base = Typography()
        base.copy(
            displayLarge = base.displayLarge.copy(fontFamily = family),
            displayMedium = base.displayMedium.copy(fontFamily = family),
            displaySmall = base.displaySmall.copy(fontFamily = family),
            headlineLarge = base.headlineLarge.copy(fontFamily = family),
            headlineMedium = base.headlineMedium.copy(fontFamily = family),
            headlineSmall = base.headlineSmall.copy(fontFamily = family),
            titleLarge = base.titleLarge.copy(fontFamily = family),
            titleMedium = base.titleMedium.copy(fontFamily = family),
            titleSmall = base.titleSmall.copy(fontFamily = family),
            bodyLarge = base.bodyLarge.copy(fontFamily = family),
            bodyMedium = base.bodyMedium.copy(fontFamily = family),
            bodySmall = base.bodySmall.copy(fontFamily = family),
            labelLarge = base.labelLarge.copy(fontFamily = family),
            labelMedium = base.labelMedium.copy(fontFamily = family),
            labelSmall = base.labelSmall.copy(fontFamily = family),
            displayLargeEmphasized = base.displayLargeEmphasized.copy(fontFamily = family),
            displayMediumEmphasized = base.displayMediumEmphasized.copy(fontFamily = family),
            displaySmallEmphasized = base.displaySmallEmphasized.copy(fontFamily = family),
            headlineLargeEmphasized = base.headlineLargeEmphasized.copy(fontFamily = family),
            headlineMediumEmphasized = base.headlineMediumEmphasized.copy(fontFamily = family),
            headlineSmallEmphasized = base.headlineSmallEmphasized.copy(fontFamily = family),
            titleLargeEmphasized = base.titleLargeEmphasized.copy(fontFamily = family),
            titleMediumEmphasized = base.titleMediumEmphasized.copy(fontFamily = family),
            titleSmallEmphasized = base.titleSmallEmphasized.copy(fontFamily = family),
            bodyLargeEmphasized = base.bodyLargeEmphasized.copy(fontFamily = family),
            bodyMediumEmphasized = base.bodyMediumEmphasized.copy(fontFamily = family),
            bodySmallEmphasized = base.bodySmallEmphasized.copy(fontFamily = family),
            labelLargeEmphasized = base.labelLargeEmphasized.copy(fontFamily = family),
            labelMediumEmphasized = base.labelMediumEmphasized.copy(fontFamily = family),
            labelSmallEmphasized = base.labelSmallEmphasized.copy(fontFamily = family),
        )
    }
}

/**
 * Tabular figures keep changing numbers (temperatures, times) from shifting width.
 * Apply on top of a role: `MaterialTheme.typography.displayLarge.tabularNumbers()`.
 */
fun TextStyle.tabularNumbers(): TextStyle = copy(fontFeatureSettings = "tnum")
