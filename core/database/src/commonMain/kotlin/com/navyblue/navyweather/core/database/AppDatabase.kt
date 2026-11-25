package com.navyblue.navyweather.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.navyblue.navyweather.core.database.dao.FavoriteCityDao
import com.navyblue.navyweather.core.database.dao.ForecastDao
import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Single Room database of the app. Room needs every entity in one `@Database`,
 * which is why entities and DAOs live in core:database instead of each feature.
 */
@Database(
    entities = [FavoriteCityEntity::class, CurrentWeatherEntity::class, DailyForecastEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao
    abstract fun forecastDao(): ForecastDao
}

/** Implemented by Room's KSP processor for every target. */
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

internal const val DATABASE_NAME = "navyweather.db"

/** Same SQLite (bundled) and the same I/O dispatcher on every platform. */
fun RoomDatabase.Builder<AppDatabase>.buildAppDatabase(): AppDatabase =
    setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
