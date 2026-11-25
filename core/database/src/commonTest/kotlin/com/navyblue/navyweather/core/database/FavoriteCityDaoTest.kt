package com.navyblue.navyweather.core.database

import app.cash.turbine.test
import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FavoriteCityDaoTest {

    private lateinit var database: AppDatabase
    private val dao get() = database.favoriteCityDao()

    @BeforeTest
    fun setUp() {
        database = inMemoryDatabaseBuilder().buildAppDatabase()
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observeAll emits newest favorites first and reacts to changes`() = runTest {
        dao.observeAll().test {
            assertEquals(emptyList(), awaitItem())

            dao.upsert(lima.copy(addedAtEpochMillis = 1))
            assertEquals(listOf(lima.copy(addedAtEpochMillis = 1)), awaitItem())

            dao.upsert(madrid.copy(addedAtEpochMillis = 2))
            assertEquals(listOf("Madrid", "Lima"), awaitItem().map { it.name })

            dao.deleteById(lima.id)
            assertEquals(listOf("Madrid"), awaitItem().map { it.name })
        }
    }

    @Test
    fun `saving the same place twice keeps one entry with the latest values`() = runTest {
        dao.upsert(lima)
        dao.upsert(lima.copy(name = "Lima Metropolitana"))

        dao.observeAll().test {
            assertEquals(listOf("Lima Metropolitana"), awaitItem().map { it.name })
        }
    }

    @Test
    fun `favorite gains its temperature when weather is stored`() = runTest {
        dao.upsert(lima)

        dao.observeAllWithWeather().test {
            assertNull(awaitItem().single().temperatureCelsius)

            database.forecastDao().replaceForecast(
                CurrentWeatherEntity(lima.id, 19.0, 18.0, 80, 8.0, 2, "2026-11-25T09:00", fetchedAtEpochMillis = 42),
                daily = emptyList(),
            )

            val updated = awaitItem().single()
            assertEquals(19.0, updated.temperatureCelsius)
            assertEquals(42, updated.weatherFetchedAtEpochMillis)
        }
    }

    @Test
    fun `observeIsFavorite follows upsert and delete`() = runTest {
        dao.observeIsFavorite(lima.id).test {
            assertFalse(awaitItem())
            dao.upsert(lima)
            assertTrue(awaitItem())
            dao.deleteById(lima.id)
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `observeAllWithWeather joins the cached temperature`() = runTest {
        dao.upsert(lima)
        dao.upsert(madrid.copy(addedAtEpochMillis = 1))
        database.forecastDao().upsertCurrent(
            CurrentWeatherEntity(lima.id, 21.5, 21.0, 70, 10.0, 1, "2026-11-25T10:00", fetchedAtEpochMillis = 99),
        )

        dao.observeAllWithWeather().test {
            val rows = awaitItem().associateBy { it.city.name }
            assertEquals(21.5, rows.getValue("Lima").temperatureCelsius)
            assertEquals(99, rows.getValue("Lima").weatherFetchedAtEpochMillis)
            assertEquals(null, rows.getValue("Madrid").temperatureCelsius)
        }
    }

    private companion object {
        val lima = FavoriteCityEntity(3936456, "Lima", "Peru", -12.04, -77.03, addedAtEpochMillis = 0)
        val madrid = FavoriteCityEntity(3117735, "Madrid", "Spain", 40.42, -3.70, addedAtEpochMillis = 0)
    }
}
