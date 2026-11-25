package com.navyblue.navyweather.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** No wallpaper-based color on this platform: the brand scheme is used. */
@Composable
internal actual fun platformDynamicColorScheme(darkTheme: Boolean): ColorScheme? = null
