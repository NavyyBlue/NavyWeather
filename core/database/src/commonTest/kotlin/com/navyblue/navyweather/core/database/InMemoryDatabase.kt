package com.navyblue.navyweather.core.database

import androidx.room.RoomDatabase

/** Room's context-free in-memory builder exists on JVM and native, so each test target provides it. */
expect fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
