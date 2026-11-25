package com.navyblue.navyweather.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.navyblue.navyweather.core.designsystem.theme.Spacing

/**
 * Shared layout for full-screen empty and error states:
 * a tonal icon badge, a title, a supporting message and an optional action.
 * Each element uses its role pair (`onX` on `X`), so contrast holds in light and dark.
 */
@Composable
internal fun StateMessage(
    icon: ImageVector,
    title: String,
    message: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    // Scrolls when the content is taller than the window (landscape, 200% text),
    // so the action is always reachable; stays centered when it fits.
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl, vertical = Spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            // Keep the message at a comfortable reading width on large windows.
            modifier = Modifier.widthIn(max = 360.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Surface(shape = CircleShape, color = containerColor, contentColor = contentColor) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // Decorative: the title carries the meaning.
                    modifier = Modifier.padding(Spacing.lg).size(32.dp),
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (action != null) {
                Box(Modifier.padding(top = Spacing.sm)) { action() }
            }
        }
    }
}
