package com.navyblue.navyweather.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.AppDatabase
import com.navyblue.navyweather.core.database.DATABASE_NAME
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal actual fun platformDatabaseModule(): Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        Room.databaseBuilder<AppDatabase>(name = databaseFilePath())
    }
}

/** The database file, inside Application Support. Internal so tests can check its location. */
internal fun databaseFilePath(): String = "${applicationSupportDirectory()}/$DATABASE_NAME"

// Application Support: app data that is not user-created and is not shown in the Files app.
@OptIn(ExperimentalForeignApi::class)
private fun applicationSupportDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path) { "Application Support directory not found" }
}
