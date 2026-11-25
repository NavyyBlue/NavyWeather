package com.navyblue.navyweather.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID

actual fun fileDatabaseBuilder(path: String): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(name = path)

actual fun uniqueTempDatabasePath(): String =
    "${NSTemporaryDirectory()}test-${NSUUID().UUIDString}.db"

@OptIn(ExperimentalForeignApi::class)
actual fun deleteDatabaseFiles(path: String) {
    listOf("", "-wal", "-shm", "-journal").forEach {
        NSFileManager.defaultManager.removeItemAtPath(path + it, error = null)
    }
}
