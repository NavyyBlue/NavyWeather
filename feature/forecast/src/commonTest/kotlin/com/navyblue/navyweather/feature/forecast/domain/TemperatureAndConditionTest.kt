package com.navyblue.navyweather.feature.forecast.domain

import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.feature.forecast.domain.model.Temperature
import com.navyblue.navyweather.feature.forecast.domain.model.WeatherCondition
import kotlin.test.Test
import kotlin.test.assertEquals

class TemperatureAndConditionTest {

    @Test
    fun `celsius is rounded to the nearest degree`() {
        assertEquals(22, Temperature(21.6).roundedIn(TemperatureUnit.CELSIUS))
        assertEquals(-3, Temperature(-2.6).roundedIn(TemperatureUnit.CELSIUS))
    }

    @Test
    fun `fahrenheit conversion`() {
        assertEquals(32, Temperature(0.0).roundedIn(TemperatureUnit.FAHRENHEIT))
        assertEquals(72, Temperature(22.0).roundedIn(TemperatureUnit.FAHRENHEIT))
    }

    @Test
    fun `wmo codes map to conditions`() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromWmoCode(0))
        assertEquals(WeatherCondition.FOG, WeatherCondition.fromWmoCode(48))
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromWmoCode(63))
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCondition.fromWmoCode(99))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromWmoCode(42))
    }
}
