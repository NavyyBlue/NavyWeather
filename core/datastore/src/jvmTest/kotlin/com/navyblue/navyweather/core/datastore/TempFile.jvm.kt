package com.navyblue.navyweather.core.datastore

import java.io.File
import java.util.UUID

actual fun uniqueTempPreferencesPath(): String =
    File(System.getProperty("java.io.tmpdir"), "test-${UUID.randomUUID()}.preferences_pb").absolutePath

actual fun deleteTempFile(path: String) {
    File(path).delete()
}
