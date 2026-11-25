package com.navyblue.navyweather.core.database

import androidx.room.RoomDatabase

/** A database stored in a file, as the app uses it, at [path]. */
expect fun fileDatabaseBuilder(path: String): RoomDatabase.Builder<AppDatabase>

/** A unique, not-yet-existing database file path in the platform's temp directory. */
expect fun uniqueTempDatabasePath(): String

/** Deletes the database file and its journal files. */
expect fun deleteDatabaseFiles(path: String)
