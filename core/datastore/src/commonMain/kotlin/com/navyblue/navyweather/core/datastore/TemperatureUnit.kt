package com.navyblue.navyweather.core.datastore

import kotlin.math.roundToInt

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT,
    ;

    /** Rounded value of a Celsius temperature in this unit, as weather apps display it ("22°"). */
    fun fromCelsius(celsius: Double): Int = when (this) {
        CELSIUS -> celsius
        FAHRENHEIT -> celsius * 9 / 5 + 32
    }.roundToInt()
}
