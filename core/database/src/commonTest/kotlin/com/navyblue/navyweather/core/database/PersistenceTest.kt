package com.navyblue.navyweather.core.database

import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** The cache survives restarts (specs/local-cache). */
class PersistenceTest {

    private val path = uniqueTempDatabasePath()

    @AfterTest
    fun tearDown() = deleteDatabaseFiles(path)

    @Test
    fun `data stored before closing the database is there after reopening it`() = runTest {
        val lima = FavoriteCityEntity(3936456, "Lima", "Peru", -12.04, -77.03, addedAtEpochMillis = 1)
        val current = CurrentWeatherEntity(lima.id, 21.0, 20.0, 70, 9.0, 1, "2026-11-25T10:00", fetchedAtEpochMillis = 7)
        val day = DailyForecastEntity(lima.id, "2026-11-25", 1, 24.0, 16.0)

        // First launch.
        fileDatabaseBuilder(path).buildAppDatabase().apply {
            favoriteCityDao().upsert(lima)
            forecastDao().replaceForecast(current, listOf(day))
            close()
        }

        // Second launch, same file.
        val reopened = fileDatabaseBuilder(path).buildAppDatabase()
        try {
            assertEquals(listOf(lima), reopened.favoriteCityDao().observeAll().first())
            assertEquals(current, reopened.forecastDao().observeCurrent(lima.id).first())
            assertEquals(listOf(day), reopened.forecastDao().observeDaily(lima.id).first())
        } finally {
            reopened.close()
        }
    }
}
