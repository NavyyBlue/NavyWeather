package com.navyblue.navyweather.shared.placeholder

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.navyblue.navyweather.core.designsystem.component.EmptyState
import com.navyblue.navyweather.shared.resources.Res
import com.navyblue.navyweather.shared.resources.navigate_back
import com.navyblue.navyweather.shared.resources.placeholder_message
import com.navyblue.navyweather.shared.resources.placeholder_title
import org.jetbrains.compose.resources.stringResource

/**
 * Stand-in for a screen that a later phase delivers, so navigation and Back already work.
 * Removed when the real feature replaces it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaceholderScreen(title: String, onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(Res.string.navigate_back))
                    }
                },
            )
        },
    ) { innerPadding ->
        EmptyState(
            icon = Icons.Rounded.Build,
            title = stringResource(Res.string.placeholder_title),
            message = stringResource(Res.string.placeholder_message),
            modifier = Modifier.padding(innerPadding),
        )
    }
}
