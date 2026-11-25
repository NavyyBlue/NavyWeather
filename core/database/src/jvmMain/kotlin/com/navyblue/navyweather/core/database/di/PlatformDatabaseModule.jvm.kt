package com.navyblue.navyweather.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.AppDatabase
import com.navyblue.navyweather.core.database.DATABASE_NAME
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

// The jvm target is only a test host; the file goes to the temp directory.
internal actual fun platformDatabaseModule(): Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        Room.databaseBuilder<AppDatabase>(name = File(System.getProperty("java.io.tmpdir"), DATABASE_NAME).absolutePath)
    }
}
