package com.navyblue.navyweather.core.designsystem.component

import androidx.compose.runtime.Composable
import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.designsystem.resources.Res
import com.navyblue.navyweather.core.designsystem.resources.error_data
import com.navyblue.navyweather.core.designsystem.resources.error_network
import com.navyblue.navyweather.core.designsystem.resources.error_server
import com.navyblue.navyweather.core.designsystem.resources.error_unknown
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** One place that turns domain errors into words: what happened and what to do next. */
internal fun AppError.messageResource(): StringResource = when (this) {
    AppError.Network -> Res.string.error_network
    is AppError.Http -> Res.string.error_server
    AppError.Serialization -> Res.string.error_data
    is AppError.Unknown -> Res.string.error_unknown
}

@Composable
fun AppError.message(): String = stringResource(messageResource())

/** Suspend variant for snackbars launched from effects. */
suspend fun AppError.loadMessage(): String = getString(messageResource())
