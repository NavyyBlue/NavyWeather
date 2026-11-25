package com.navyblue.navyweather.core.weather

import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.AppDatabase

/** Room's context-free in-memory builder exists on JVM and native, so each test target provides it. */
expect fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
