package com.navyblue.navyweather.core.weather

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.database.AppDatabase
import com.navyblue.navyweather.core.database.buildAppDatabase
import com.navyblue.navyweather.core.database.dao.ForecastDao
import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import androidx.sqlite.SQLiteException
import com.navyblue.navyweather.core.network.HttpClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/** Downloaded forecasts are written into the cache, and failures keep it intact (specs/weather-data). */
class ForecastSyncTest {

    private lateinit var database: AppDatabase
    private val dao get() = database.forecastDao()

    private var status = HttpStatusCode.OK
    private var body = forecastJson()

    private val clock = object : Clock {
        var now = Instant.fromEpochMilliseconds(1_000)
        override fun now() = now
    }

    private val sync by lazy {
        ForecastSync(
            api = OpenMeteoForecastApi(
                HttpClientFactory.create(
                    MockEngine { respond(body, status, headersOf(HttpHeaders.ContentType, "application/json")) },
                ),
            ),
            dao = dao,
            clock = clock,
        )
    }

    @BeforeTest
    fun setUp() {
        database = inMemoryDatabaseBuilder().buildAppDatabase()
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun `successful download is stored with its download time`() = runTest {
        clock.now = Instant.fromEpochMilliseconds(1_700_000_000_000)

        val result = sync.sync(CITY_ID, latitude = -12.05, longitude = -77.04)

        assertEquals(AppResult.Success(Unit), result)
        val current = requireNotNull(dao.observeCurrent(CITY_ID).first())
        assertEquals(21.4, current.temperatureCelsius)
        assertEquals(1_700_000_000_000, current.fetchedAtEpochMillis)
        assertEquals(7, dao.observeDaily(CITY_ID).first().size)
    }

    @Test
    fun `last download time is absent before the first download and set after it`() = runTest {
        assertNull(sync.lastSyncedAtEpochMillis(CITY_ID))

        clock.now = Instant.fromEpochMilliseconds(5_000)
        sync.sync(CITY_ID, latitude = -12.05, longitude = -77.04)

        assertEquals(5_000, sync.lastSyncedAtEpochMillis(CITY_ID))
    }

    @Test
    fun `failed download keeps the cached forecast and reports the server error`() = runTest {
        sync.sync(CITY_ID, latitude = -12.05, longitude = -77.04)
        val cachedCurrent = dao.observeCurrent(CITY_ID).first()
        val cachedDaily = dao.observeDaily(CITY_ID).first()

        status = HttpStatusCode.ServiceUnavailable
        body = "{}"
        clock.now = Instant.fromEpochMilliseconds(9_000)
        val result = sync.sync(CITY_ID, latitude = -12.05, longitude = -77.04)

        assertEquals(AppResult.Failure(AppError.Http(503)), result)
        assertEquals(cachedCurrent, dao.observeCurrent(CITY_ID).first())
        assertEquals(cachedDaily, dao.observeDaily(CITY_ID).first())
    }

    @Test
    fun `uneven daily lists keep only the days present in every list`() = runTest {
        body = forecastJson(days = 7, temperatureDays = 6)

        sync.sync(CITY_ID, latitude = -12.05, longitude = -77.04)

        val days = dao.observeDaily(CITY_ID).first()
        assertEquals(6, days.size)
        assertEquals("2026-11-30", days.last().date)
    }

    @Test
    fun `failed cache write is reported as an error instead of thrown`() = runTest {
        val failingDao = object : ForecastDao by dao {
            override suspend fun replaceForecast(current: CurrentWeatherEntity, daily: List<DailyForecastEntity>) =
                throw SQLiteException("database or disk is full")
        }
        val failingSync = ForecastSync(
            api = OpenMeteoForecastApi(
                HttpClientFactory.create(
                    MockEngine { respond(body, status, headersOf(HttpHeaders.ContentType, "application/json")) },
                ),
            ),
            dao = failingDao,
            clock = clock,
        )

        val result = failingSync.sync(CITY_ID, latitude = -12.05, longitude = -77.04)

        assertTrue(result is AppResult.Failure && result.error is AppError.Unknown, "$result")
    }

    private companion object {
        const val CITY_ID = 3936456L
    }
}
