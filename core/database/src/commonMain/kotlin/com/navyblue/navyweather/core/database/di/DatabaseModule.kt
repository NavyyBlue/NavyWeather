package com.navyblue.navyweather.core.database.di

import com.navyblue.navyweather.core.database.AppDatabase
import com.navyblue.navyweather.core.database.buildAppDatabase
import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/** Provides a `RoomDatabase.Builder<AppDatabase>` with the platform-specific file location. */
internal expect fun platformDatabaseModule(): Module

val databaseModule = module {
    includes(platformDatabaseModule())
    single { get<RoomDatabase.Builder<AppDatabase>>().buildAppDatabase() }
    single { get<AppDatabase>().favoriteCityDao() }
    single { get<AppDatabase>().forecastDao() }
}
