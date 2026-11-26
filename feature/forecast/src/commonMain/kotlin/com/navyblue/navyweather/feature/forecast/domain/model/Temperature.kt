package com.navyblue.navyweather.feature.forecast.domain.model

import com.navyblue.navyweather.core.datastore.TemperatureUnit
import kotlin.jvm.JvmInline

/** A temperature stored in Celsius (as the API and cache use) and shown in the user's unit. */
@JvmInline
value class Temperature(val celsius: Double) {

    /** Rounded value in [unit], the way weather apps display it ("22°"). */
    fun roundedIn(unit: TemperatureUnit): Int = unit.fromCelsius(celsius)
}
