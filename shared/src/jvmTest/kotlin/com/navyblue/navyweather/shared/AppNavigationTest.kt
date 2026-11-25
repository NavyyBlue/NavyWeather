package com.navyblue.navyweather.shared

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.room.Room
import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.AppDatabase
import com.navyblue.navyweather.core.database.buildAppDatabase
import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import com.navyblue.navyweather.core.network.HttpClientFactory
import com.navyblue.navyweather.shared.di.appModules
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Screens are connected through typed routes (specs/app-shell): the real App() with the real
 * Koin graph, where only the database (in memory, seeded) and the network (offline) are replaced.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class AppNavigationTest {

    private val originalLocale = Locale.getDefault()
    private lateinit var database: AppDatabase

    @BeforeTest
    fun setUp() {
        // Navigation and lifecycle require "the main thread", which they detect through
        // Dispatchers.Main; an unconfined Main makes the Compose test thread qualify.
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Locale.setDefault(Locale.ENGLISH) // Assertions use the English texts.
        database = Room.inMemoryDatabaseBuilder<AppDatabase>().buildAppDatabase()
        runBlocking { database.favoriteCityDao().upsert(LIMA) }
        startKoin {
            allowOverride(true)
            modules(
                appModules(enableHttpLogging = false) + module {
                    single<RoomDatabase.Builder<AppDatabase>> { error("replaced by the in-memory database") }
                    single { database }
                    // DataStore allows one instance per file per process; each test gets its own Koin.
                    single<UserPreferencesRepository> { InMemoryPreferences() }
                    // Offline: background refreshes fail silently and the list stays as cached.
                    single<HttpClient> { HttpClientFactory.create(MockEngine { respondError(HttpStatusCode.ServiceUnavailable) }) }
                },
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        database.close()
        Locale.setDefault(originalLocale)
        Dispatchers.resetMain()
    }

    @Test
    fun tappingAFavoriteOpensItsForecastAndBackReturnsToFavorites() = runComposeUiTest {
        setContent { App() }

        waitUntil(timeoutMillis = 5_000) { runCatching { onNodeWithText("Lima").assertIsDisplayed() }.isSuccess }
        onNodeWithText("Lima").performClick()

        // The forecast destination received the route of that place.
        waitUntil { runCatching { onNodeWithText("Lima, Peru").assertIsDisplayed() }.isSuccess }
        onNodeWithText("Coming soon").assertIsDisplayed()

        onNodeWithContentDescription("Back").performClick()

        waitUntil { runCatching { onNodeWithText("Your places").assertIsDisplayed() }.isSuccess }
        onNodeWithText("Lima").assertIsDisplayed()
    }

    @Test
    fun searchOpensItsPlaceholderWithAWayBack() = runComposeUiTest {
        setContent { App() }

        waitUntil(timeoutMillis = 5_000) { runCatching { onNodeWithText("Lima").assertIsDisplayed() }.isSuccess }
        onNodeWithText("Search city", useUnmergedTree = true).performClick()

        waitUntil { runCatching { onNodeWithText("Search").assertIsDisplayed() }.isSuccess }
        onNodeWithText("Coming soon").assertIsDisplayed()

        onNodeWithContentDescription("Back").performClick()

        waitUntil { runCatching { onNodeWithText("Your places").assertIsDisplayed() }.isSuccess }
    }

    private companion object {
        val LIMA = FavoriteCityEntity(3936456, "Lima", "Peru", -12.05, -77.04, addedAtEpochMillis = 0)
    }
}

private class InMemoryPreferences : UserPreferencesRepository {
    override val temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        temperatureUnit.value = unit
    }
}
