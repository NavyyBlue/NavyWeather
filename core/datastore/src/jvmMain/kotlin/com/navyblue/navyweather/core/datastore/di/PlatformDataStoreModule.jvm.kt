package com.navyblue.navyweather.core.datastore.di

import com.navyblue.navyweather.core.datastore.DATA_STORE_FILE_NAME
import com.navyblue.navyweather.core.datastore.createPreferencesDataStore
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

// The jvm target is only a test host; the file goes to the temp directory.
internal actual fun platformDataStoreModule(): Module = module {
    single { createPreferencesDataStore(File(System.getProperty("java.io.tmpdir"), DATA_STORE_FILE_NAME).absolutePath) }
}
