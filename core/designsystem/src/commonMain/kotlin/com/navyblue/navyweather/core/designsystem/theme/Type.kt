package com.navyblue.navyweather.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

/**
 * Material 3 role scale (Display, Headline, Title, Body, Label).
 * Screens use the roles, never raw font sizes, so a brand typeface can be added here later
 * without touching any screen.
 */
val NavyTypography = Typography()

/**
 * Tabular figures keep changing numbers (temperatures, times) from shifting width.
 * Apply on top of a role: `MaterialTheme.typography.displayLarge.tabularNumbers()`.
 */
fun TextStyle.tabularNumbers(): TextStyle = copy(fontFeatureSettings = "tnum")
