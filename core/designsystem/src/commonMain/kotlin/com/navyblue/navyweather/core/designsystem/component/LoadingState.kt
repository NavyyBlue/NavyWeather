package com.navyblue.navyweather.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.navyblue.navyweather.core.designsystem.resources.Res
import com.navyblue.navyweather.core.designsystem.resources.loading
import org.jetbrains.compose.resources.stringResource

/**
 * Full-screen loading with Material 3 Expressive's [LoadingIndicator], meant for short waits
 * (about 200ms to 5s). Longer, measurable work should show a progress indicator instead.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.loading)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator(modifier = Modifier.semantics { contentDescription = description })
    }
}
