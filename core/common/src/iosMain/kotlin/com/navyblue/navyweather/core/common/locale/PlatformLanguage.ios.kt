package com.navyblue.navyweather.core.common.locale

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

internal actual fun platformLanguageCode(): String = NSLocale.currentLocale.languageCode.ifBlank { "en" }
