package com.navyblue.navyweather.core.datastore.di

import com.navyblue.navyweather.core.datastore.DATA_STORE_FILE_NAME
import com.navyblue.navyweather.core.datastore.createPreferencesDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal actual fun platformDataStoreModule(): Module = module {
    single { createPreferencesDataStore("${applicationSupportDirectory()}/$DATA_STORE_FILE_NAME") }
}

// Application Support: app data that is not user-created and is not shown in the Files app.
@OptIn(ExperimentalForeignApi::class)
private fun applicationSupportDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path) { "Application Support directory not found" }
}
