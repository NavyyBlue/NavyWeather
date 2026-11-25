package com.navyblue.navyweather.core.datastore

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
