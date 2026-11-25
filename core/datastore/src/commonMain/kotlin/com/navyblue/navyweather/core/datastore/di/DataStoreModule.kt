package com.navyblue.navyweather.core.datastore.di

import com.navyblue.navyweather.core.datastore.DataStoreUserPreferencesRepository
import com.navyblue.navyweather.core.datastore.UserPreferencesRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/** Provides the `DataStore<Preferences>` stored in the platform's app-private directory. */
internal expect fun platformDataStoreModule(): Module

val dataStoreModule = module {
    includes(platformDataStoreModule())
    single<UserPreferencesRepository> { DataStoreUserPreferencesRepository(get()) }
}
