package com.navyblue.navyweather.core.datastore

import kotlin.test.Test
import kotlin.test.assertEquals

/** Temperatures are displayed as whole numbers in the chosen unit (specs/user-preferences). */
class TemperatureUnitTest {

    @Test
    fun `celsius is rounded to the nearest degree`() {
        assertEquals(22, TemperatureUnit.CELSIUS.fromCelsius(21.6))
        assertEquals(-3, TemperatureUnit.CELSIUS.fromCelsius(-3.4))
    }

    @Test
    fun `fahrenheit is converted and rounded`() {
        assertEquals(68, TemperatureUnit.FAHRENHEIT.fromCelsius(20.0))
        assertEquals(32, TemperatureUnit.FAHRENHEIT.fromCelsius(0.0))
    }
}
