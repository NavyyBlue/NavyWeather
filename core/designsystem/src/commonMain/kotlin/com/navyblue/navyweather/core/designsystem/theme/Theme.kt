package com.navyblue.navyweather.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme

/**
 * App theme shared by Android and iOS, built on Material 3 Expressive.
 *
 * - Color: a brand scheme generated from [NavySeed] with the Fidelity style, which keeps the
 *   seed itself as `primaryContainer` (the app reads as navy) and derives a warm orange
 *   `tertiary` (navy's complement). Usage rules, following Material's emphasis guidance:
 *     - `primaryContainer` (strong navy fill) only on the hero element of a screen,
 *       such as the current-temperature card in the forecast.
 *     - `tertiary` / `tertiaryContainer` only as a status accent, never as a card fill.
 *     - Lists and everything else stay on surface roles.
 *   Wallpaper-based dynamic color is opt-in through [dynamicColor]: it only exists on
 *   Android 12+, and the brand scheme keeps both platforms looking the same.
 * - Motion: the expressive spring scheme, used by Material components and by any custom
 *   animation through `MaterialTheme.motionScheme`.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavyWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val brandScheme = rememberDynamicColorScheme(
        seedColor = NavySeed,
        isDark = darkTheme,
        style = PaletteStyle.Fidelity,
    )
    val colorScheme = (if (dynamicColor) platformDynamicColorScheme(darkTheme) else null) ?: brandScheme

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = navyTypography(),
        shapes = NavyShapes,
        content = content,
    )
}

/** Wallpaper-based scheme where the platform supports it (Android 12+), `null` elsewhere. */
@Composable
internal expect fun platformDynamicColorScheme(darkTheme: Boolean): ColorScheme?
