package com.navyblue.navyweather.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.navyblue.navyweather.core.designsystem.resources.Res
import com.navyblue.navyweather.core.designsystem.resources.error_title
import com.navyblue.navyweather.core.designsystem.resources.retry
import org.jetbrains.compose.resources.stringResource

/** Test tag of the retry button, so screens that embed [ErrorState] can find it in UI tests. */
const val ERROR_STATE_RETRY_TAG = "error_state_retry"

/**
 * Full-screen error. Retrying is the only way forward, so it is the primary action:
 * a filled button, the strongest emphasis on the screen.
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.error_title),
) {
    StateMessage(
        icon = Icons.Rounded.Warning,
        title = title,
        message = message,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier,
        action = {
            Button(onClick = onRetry, modifier = Modifier.testTag(ERROR_STATE_RETRY_TAG), contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(Res.string.retry))
            }
        },
    )
}
