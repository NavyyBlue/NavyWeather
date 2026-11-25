package com.navyblue.navyweather.core.datastore

/** A unique, not-yet-existing file path in the platform's temp directory. */
expect fun uniqueTempPreferencesPath(): String

/** Deletes a file created by a test; no-op if it does not exist. */
expect fun deleteTempFile(path: String)

/** Overwrites [path] with raw [bytes], to simulate a corrupted file. */
expect fun writeRawFile(path: String, bytes: ByteArray)
