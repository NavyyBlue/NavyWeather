package com.navyblue.navyweather.core.database

import app.cash.turbine.test
import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ForecastDaoTest {

    private lateinit var database: AppDatabase
    private val dao get() = database.forecastDao()

    @BeforeTest
    fun setUp() {
        database = inMemoryDatabaseBuilder().buildAppDatabase()
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun `replaceForecast stores current weather`() = runTest {
        dao.observeCurrent(CITY_ID).test {
            assertNull(awaitItem())
            dao.replaceForecast(current(temperature = 20.0), daily("2026-11-25", "2026-11-26"))
            assertEquals(20.0, awaitItem()?.temperatureCelsius)
        }
    }

    @Test
    fun `replaceForecast drops days that are no longer in the forecast`() = runTest {
        dao.replaceForecast(current(temperature = 20.0), daily("2026-11-24", "2026-11-25"))
        dao.replaceForecast(current(temperature = 22.0), daily("2026-11-25", "2026-11-26"))

        dao.observeDaily(CITY_ID).test {
            assertEquals(listOf("2026-11-25", "2026-11-26"), awaitItem().map { it.date })
        }
    }

    @Test
    fun `days are read back ordered by date`() = runTest {
        dao.replaceForecast(current(temperature = 20.0), daily("2026-11-27", "2026-11-25", "2026-11-26"))

        dao.observeDaily(CITY_ID).test {
            assertEquals(listOf("2026-11-25", "2026-11-26", "2026-11-27"), awaitItem().map { it.date })
        }
    }

    @Test
    fun `repeated date does not fail the replacement`() = runTest {
        dao.replaceForecast(current(temperature = 20.0), daily("2026-11-25", "2026-11-25", "2026-11-26"))

        dao.observeDaily(CITY_ID).test {
            assertEquals(listOf("2026-11-25", "2026-11-26"), awaitItem().map { it.date })
        }
        dao.observeCurrent(CITY_ID).test {
            assertEquals(20.0, awaitItem()?.temperatureCelsius)
        }
    }

    private fun current(temperature: Double) = CurrentWeatherEntity(
        cityId = CITY_ID,
        temperatureCelsius = temperature,
        apparentTemperatureCelsius = temperature,
        relativeHumidity = 70,
        windSpeedKmh = 12.0,
        weatherCode = 1,
        observedAt = "2026-11-25T10:00",
        fetchedAtEpochMillis = 0,
    )

    private fun daily(vararg dates: String) = dates.map { date ->
        DailyForecastEntity(CITY_ID, date, weatherCode = 3, maxTemperatureCelsius = 24.0, minTemperatureCelsius = 16.0)
    }

    private companion object {
        const val CITY_ID = 3936456L
    }
}
