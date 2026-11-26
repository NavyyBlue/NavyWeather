package com.navyblue.navyweather.feature.forecast.presentation

import com.navyblue.navyweather.feature.forecast.domain.model.WeatherCondition
import com.navyblue.navyweather.feature.forecast.resources.Res
import com.navyblue.navyweather.feature.forecast.resources.condition_clear
import com.navyblue.navyweather.feature.forecast.resources.condition_drizzle
import com.navyblue.navyweather.feature.forecast.resources.condition_fog
import com.navyblue.navyweather.feature.forecast.resources.condition_mainly_clear
import com.navyblue.navyweather.feature.forecast.resources.condition_overcast
import com.navyblue.navyweather.feature.forecast.resources.condition_partly_cloudy
import com.navyblue.navyweather.feature.forecast.resources.condition_rain
import com.navyblue.navyweather.feature.forecast.resources.condition_showers
import com.navyblue.navyweather.feature.forecast.resources.condition_snow
import com.navyblue.navyweather.feature.forecast.resources.condition_thunderstorm
import com.navyblue.navyweather.feature.forecast.resources.condition_unknown
import org.jetbrains.compose.resources.StringResource

/** Label for screen readers and text; the glyph is decorative and always paired with the label. */
internal val WeatherCondition.label: StringResource
    get() = when (this) {
        WeatherCondition.CLEAR -> Res.string.condition_clear
        WeatherCondition.MAINLY_CLEAR -> Res.string.condition_mainly_clear
        WeatherCondition.PARTLY_CLOUDY -> Res.string.condition_partly_cloudy
        WeatherCondition.OVERCAST -> Res.string.condition_overcast
        WeatherCondition.FOG -> Res.string.condition_fog
        WeatherCondition.DRIZZLE -> Res.string.condition_drizzle
        WeatherCondition.RAIN -> Res.string.condition_rain
        WeatherCondition.SNOW -> Res.string.condition_snow
        WeatherCondition.SHOWERS -> Res.string.condition_showers
        WeatherCondition.THUNDERSTORM -> Res.string.condition_thunderstorm
        WeatherCondition.UNKNOWN -> Res.string.condition_unknown
    }

/** Color glyph that depicts the sky; rendered by the platform emoji font on Android and iOS. */
internal val WeatherCondition.glyph: String
    get() = when (this) {
        WeatherCondition.CLEAR -> "☀️"
        WeatherCondition.MAINLY_CLEAR -> "🌤️"
        WeatherCondition.PARTLY_CLOUDY -> "⛅"
        WeatherCondition.OVERCAST -> "☁️"
        WeatherCondition.FOG -> "🌫️"
        WeatherCondition.DRIZZLE -> "🌦️"
        WeatherCondition.RAIN -> "🌧️"
        WeatherCondition.SNOW -> "🌨️"
        WeatherCondition.SHOWERS -> "🌦️"
        WeatherCondition.THUNDERSTORM -> "⛈️"
        WeatherCondition.UNKNOWN -> "🌡️"
    }
