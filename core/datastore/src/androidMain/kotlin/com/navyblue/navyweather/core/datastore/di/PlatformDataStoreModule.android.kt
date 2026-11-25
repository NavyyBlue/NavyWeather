package com.navyblue.navyweather.core.datastore.di

import com.navyblue.navyweather.core.datastore.DATA_STORE_FILE_NAME
import com.navyblue.navyweather.core.datastore.createPreferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformDataStoreModule(): Module = module {
    single { createPreferencesDataStore(androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath) }
}
