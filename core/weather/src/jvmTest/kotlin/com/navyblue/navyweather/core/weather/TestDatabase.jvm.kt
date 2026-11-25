package com.navyblue.navyweather.core.weather

import androidx.room.Room
import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.AppDatabase

actual fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
    Room.inMemoryDatabaseBuilder<AppDatabase>()
