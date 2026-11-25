package com.navyblue.navyweather.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.AppDatabase
import com.navyblue.navyweather.core.database.DATABASE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformDatabaseModule(): Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val context = androidContext()
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = context.getDatabasePath(DATABASE_NAME).absolutePath,
        )
    }
}
