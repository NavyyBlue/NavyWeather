package com.navyblue.navyweather.feature.forecast.domain.model

/** Weather conditions the app distinguishes, grouped from WMO weather codes. */
enum class WeatherCondition {
    CLEAR,
    MAINLY_CLEAR,
    PARTLY_CLOUDY,
    OVERCAST,
    FOG,
    DRIZZLE,
    RAIN,
    SNOW,
    SHOWERS,
    THUNDERSTORM,
    UNKNOWN,
    ;

    companion object {
        /** WMO weather interpretation codes, as documented by Open-Meteo. */
        fun fromWmoCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1 -> MAINLY_CLEAR
            2 -> PARTLY_CLOUDY
            3 -> OVERCAST
            45, 48 -> FOG
            51, 53, 55, 56, 57 -> DRIZZLE
            61, 63, 65, 66, 67 -> RAIN
            71, 73, 75, 77, 85, 86 -> SNOW
            80, 81, 82 -> SHOWERS
            95, 96, 99 -> THUNDERSTORM
            else -> UNKNOWN
        }
    }
}
