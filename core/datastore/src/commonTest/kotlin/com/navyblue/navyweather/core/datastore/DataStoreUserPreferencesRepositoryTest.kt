package com.navyblue.navyweather.core.datastore

import app.cash.turbine.test
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Temperature unit preference (specs/user-preferences). */
class DataStoreUserPreferencesRepositoryTest {

    private val path = uniqueTempPreferencesPath()

    @AfterTest
    fun tearDown() = deleteTempFile(path)

    @Test
    fun `defaults to Celsius on a fresh install`() = runTest {
        val repository = DataStoreUserPreferencesRepository(createPreferencesDataStore(path, scope = backgroundScope))

        assertEquals(TemperatureUnit.CELSIUS, repository.temperatureUnit.first())
    }

    @Test
    fun `selected unit survives a restart`() = runTest {
        // First "launch": write Fahrenheit, then release the file as the process would on exit.
        withDataStoreScope { scope ->
            DataStoreUserPreferencesRepository(createPreferencesDataStore(path, scope))
                .setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        }

        // Second "launch": a new instance on the same file.
        val restarted = DataStoreUserPreferencesRepository(createPreferencesDataStore(path, scope = backgroundScope))

        assertEquals(TemperatureUnit.FAHRENHEIT, restarted.temperatureUnit.first())
    }

    @Test
    fun `selecting the same unit again does not notify observers`() = runTest {
        val repository = DataStoreUserPreferencesRepository(createPreferencesDataStore(path, scope = backgroundScope))

        repository.temperatureUnit.test {
            assertEquals(TemperatureUnit.CELSIUS, awaitItem())

            repository.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
            assertEquals(TemperatureUnit.FAHRENHEIT, awaitItem())

            repository.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
            expectNoEvents()
        }
    }

    @Test
    fun `unknown stored value is read as Celsius`() = runTest {
        val dataStore = createPreferencesDataStore(path, scope = backgroundScope)
        dataStore.edit { it[stringPreferencesKey("temperature_unit")] = "KELVIN" }

        val repository = DataStoreUserPreferencesRepository(dataStore)

        assertEquals(TemperatureUnit.CELSIUS, repository.temperatureUnit.first())
    }

    @Test
    fun `corrupted preferences file is read as the defaults`() = runTest {
        writeRawFile(path, byteArrayOf(0x7F, 0x00, 0x13, 0x37, 0x42))
        val repository = DataStoreUserPreferencesRepository(createPreferencesDataStore(path, scope = backgroundScope))

        assertEquals(TemperatureUnit.CELSIUS, repository.temperatureUnit.first())
    }

    /** Runs [block] with a scope whose DataStore is fully closed (file released) when it returns. */
    private suspend fun TestScope.withDataStoreScope(block: suspend (CoroutineScope) -> Unit) {
        val job = Job()
        try {
            block(CoroutineScope(coroutineContext + job))
        } finally {
            job.cancel()
            job.join()
        }
    }
}
