package com.navyblue.navyweather.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath

internal const val DATA_STORE_FILE_NAME = "navyweather.preferences_pb"

/**
 * DataStore allows only one instance per file, so it is created once and held as a Koin `single`.
 * The file path is platform specific; everything else is shared.
 */
internal fun createPreferencesDataStore(
    path: String,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    scope = scope,
    produceFile = { path.toPath() },
)
