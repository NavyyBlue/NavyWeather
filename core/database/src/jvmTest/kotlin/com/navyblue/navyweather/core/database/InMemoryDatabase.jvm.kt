package com.navyblue.navyweather.core.database

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
    Room.inMemoryDatabaseBuilder<AppDatabase>()
