package com.navyblue.navyweather.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 8dp spacing scale. Padding and gaps go on parents, not as margins on children. */
object Spacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val xxxl: Dp = 48.dp

    /** Side margin on compact windows (under 600dp). */
    val screenHorizontal: Dp = lg
}
