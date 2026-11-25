package com.navyblue.navyweather.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.util.UUID

actual fun fileDatabaseBuilder(path: String): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(name = path)

actual fun uniqueTempDatabasePath(): String =
    File(System.getProperty("java.io.tmpdir"), "test-${UUID.randomUUID()}.db").absolutePath

actual fun deleteDatabaseFiles(path: String) {
    listOf("", "-wal", "-shm", "-journal").forEach { File(path + it).delete() }
}
