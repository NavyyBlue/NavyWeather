package com.navyblue.navyweather.shared

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.room.Room
import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.AppDatabase
import com.navyblue.navyweather.core.database.buildAppDatabase
import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.datastore.UserPreferencesRepository
import com.navyblue.navyweather.core.network.HttpClientFactory
import com.navyblue.navyweather.shared.di.appModules
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScheduler
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
 * Navigation follows typed routes and the screen history (specs/app-shell), and favorites saved
 * from a forecast appear in the list (specs/favorite-places). The real App() and Koin graph run;
 * only the database (in memory, seeded with Lima) and the network (canned Open-Meteo answers)
 * are replaced.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class AppNavigationTest {

    private val originalLocale = Locale.getDefault()
    private val scheduler = TestCoroutineScheduler()
    private lateinit var database: AppDatabase

    @BeforeTest
    fun setUp() {
        // Navigation and lifecycle require "the main thread", which they detect through
        // Dispatchers.Main; an unconfined Main makes the Compose test thread qualify. Its
        // scheduler lets the test skip the search debounce.
        Dispatchers.setMain(UnconfinedTestDispatcher(scheduler))
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
                    single<HttpClient> { HttpClientFactory.create(openMeteo) }
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
    fun forecastOpenedFromFavoritesGoesBackToFavorites() = runComposeUiTest {
        setContent { App() }
        waitForText("Lima")

        onNodeWithText("Lima").performClick()
        waitForText("Partly cloudy")

        onNodeWithContentDescription("Back").performClick()
        waitForText("Your places")
    }

    @Test
    fun forecastOpenedFromSearchGoesBackToFavorites() = runComposeUiTest {
        setContent { App() }
        openSearchAndPickTarapoto()

        onNodeWithContentDescription("Back").performClick()

        // Search was replaced by the forecast, so Back lands on favorites.
        waitForText("Your places")
    }

    @Test
    fun backFromSearchReturnsToFavorites() = runComposeUiTest {
        setContent { App() }
        waitForText("Lima")
        onNodeWithText("Search city", useUnmergedTree = true).performClick()
        waitUntil { runCatching { onNode(editableField).assertIsDisplayed() }.isSuccess }

        onNodeWithContentDescription("Back").performClick()

        waitForText("Your places")
    }

    @Test
    fun placeSavedFromItsForecastAppearsInFavorites() = runComposeUiTest {
        setContent { App() }
        openSearchAndPickTarapoto()

        onNodeWithContentDescription("Add to favorites").performClick()
        waitUntil { runCatching { onNodeWithContentDescription("Remove from favorites").assertIsDisplayed() }.isSuccess }
        onNodeWithContentDescription("Back").performClick()

        waitForText("Your places")
        waitForText("Tarapoto")
    }

    private fun ComposeUiTest.openSearchAndPickTarapoto() {
        waitForText("Lima")
        onNodeWithText("Search city", useUnmergedTree = true).performClick()
        waitUntil { runCatching { onNode(editableField).assertIsDisplayed() }.isSuccess }

        onNode(editableField).performTextInput("Tara")
        scheduler.advanceTimeBy(400) // Past the 350 ms debounce.
        waitForText("Tarapoto")

        onNodeWithText("Tarapoto").performClick()
        waitForText("Partly cloudy")
    }

    /** Waits until [text] is shown at least once (app bars can render a title in two layouts). */
    private fun ComposeUiTest.waitForText(text: String) =
        waitUntil(timeoutMillis = 5_000) { onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }

    private val editableField = SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText)

    private companion object {
        val LIMA = FavoriteCityEntity(3936456, "Lima", "Peru", -12.05, -77.04, addedAtEpochMillis = 0)

        val json = headersOf(HttpHeaders.ContentType, "application/json")

        /** Answers the two Open-Meteo APIs the app calls. */
        val openMeteo = MockEngine { request ->
            when (request.url.host) {
                "geocoding-api.open-meteo.com" -> respond(
                    """{"results":[{"id":3666304,"name":"Tarapoto","latitude":-6.49,"longitude":-76.37,"country":"Peru","admin1":"San Martín"}]}""",
                    headers = json,
                )
                else -> respond(FORECAST_JSON, headers = json)
            }
        }

        val FORECAST_JSON = """
            {
              "current": {"time": "2026-11-26T10:00", "temperature_2m": 27.6, "apparent_temperature": 29.0,
                          "relative_humidity_2m": 70, "weather_code": 2, "wind_speed_10m": 8.0},
              "daily": {"time": ["2026-11-26", "2026-11-27"], "weather_code": [2, 61],
                        "temperature_2m_max": [31.0, 30.0], "temperature_2m_min": [21.0, 22.0]}
            }
        """.trimIndent()
    }
}

private class InMemoryPreferences : UserPreferencesRepository {
    override val temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        temperatureUnit.value = unit
    }
}
