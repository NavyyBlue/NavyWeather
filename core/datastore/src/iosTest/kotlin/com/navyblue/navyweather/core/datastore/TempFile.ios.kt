package com.navyblue.navyweather.core.datastore

import platform.Foundation.writeToFile
import platform.Foundation.create
import platform.Foundation.NSData
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID

actual fun uniqueTempPreferencesPath(): String =
    "${NSTemporaryDirectory()}test-${NSUUID().UUIDString}.preferences_pb"

@OptIn(ExperimentalForeignApi::class)
actual fun deleteTempFile(path: String) {
    NSFileManager.defaultManager.removeItemAtPath(path, error = null)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun writeRawFile(path: String, bytes: ByteArray) {
    val data = bytes.usePinned { pinned -> NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong()) }
    data.writeToFile(path, atomically = true)
}
