package com.navyblue.navyweather.core.common.locale

import java.util.Locale

internal actual fun platformLanguageCode(): String = Locale.getDefault().language.ifBlank { "en" }
