package com.navyblue.navyweather.core.weather

/** A trimmed Open-Meteo forecast response, with an extra field the app does not use. */
internal fun forecastJson(
    temperature: Double = 21.4,
    days: Int = 7,
    temperatureDays: Int = days,
): String {
    val dates = (1..days).joinToString { "\"2026-11-${(24 + it).toString().padStart(2, '0')}\"" }
    val codes = (1..days).joinToString { "3" }
    val maxes = (1..temperatureDays).joinToString { "${24.0 + it}" }
    val mins = (1..temperatureDays).joinToString { "${15.0 + it}" }
    return """
        {
          "latitude": -12.05,
          "longitude": -77.04,
          "generationtime_ms": 0.1,
          "current": {
            "time": "2026-11-25T10:00",
            "temperature_2m": $temperature,
            "apparent_temperature": 22.0,
            "relative_humidity_2m": 74,
            "weather_code": 2,
            "wind_speed_10m": 11.5
          },
          "daily": {
            "time": [$dates],
            "weather_code": [$codes],
            "temperature_2m_max": [$maxes],
            "temperature_2m_min": [$mins]
          }
        }
    """.trimIndent()
}
