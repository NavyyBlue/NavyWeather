package com.navyblue.navyweather.feature.forecast.data

import app.cash.turbine.test
import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.database.dao.ForecastDao
import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import com.navyblue.navyweather.core.network.HttpClientFactory
import com.navyblue.navyweather.feature.forecast.FixedClock
import com.navyblue.navyweather.core.weather.ForecastSync
import com.navyblue.navyweather.core.weather.OpenMeteoForecastApi
import com.navyblue.navyweather.feature.forecast.data.repository.ForecastRepositoryImpl
import com.navyblue.navyweather.feature.forecast.domain.model.WeatherCondition
import com.navyblue.navyweather.feature.forecast.limaLocation
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Offline-first repository: MockEngine for the API, an in-memory DAO for the cache. */
class ForecastRepositoryImplTest {

    private class InMemoryForecastDao : ForecastDao {
        val current = MutableStateFlow<Map<Long, CurrentWeatherEntity>>(emptyMap())
        val daily = MutableStateFlow<List<DailyForecastEntity>>(emptyList())
        override fun observeCurrent(cityId: Long): Flow<CurrentWeatherEntity?> = current.map { it[cityId] }
        override fun observeDaily(cityId: Long): Flow<List<DailyForecastEntity>> =
            daily.map { rows -> rows.filter { it.cityId == cityId }.sortedBy { it.date } }
        override suspend fun upsertCurrent(current: CurrentWeatherEntity) {
            this.current.value = this.current.value + (current.cityId to current)
        }
        override suspend fun deleteDaily(cityId: Long) {
            daily.value = daily.value.filterNot { it.cityId == cityId }
        }
        override suspend fun upsertDaily(daily: List<DailyForecastEntity>) {
            this.daily.value = this.daily.value + daily
        }
    }

    private val json = """
        {"latitude":-12.04,"longitude":-77.03,
         "current":{"time":"2026-11-25T10:00","interval":900,"temperature_2m":21.4,"apparent_temperature":20.9,
                    "relative_humidity_2m":68,"weather_code":2,"wind_speed_10m":11.2},
         "daily":{"time":["2026-11-25","2026-11-26"],"weather_code":[1,61],
                  "temperature_2m_max":[24.1,22.0],"temperature_2m_min":[16.3,15.8]}}
    """.trimIndent()

    private fun repository(dao: ForecastDao, status: HttpStatusCode, body: String) = ForecastRepositoryImpl(
        sync = ForecastSync(
            api = OpenMeteoForecastApi(
                HttpClientFactory.create(
                    MockEngine { respond(body, status, headersOf(HttpHeaders.ContentType, "application/json")) },
                ),
            ),
            dao = dao,
            clock = FixedClock(nowMillis = 42),
        ),
        dao = dao,
    )

    @Test
    fun `refresh caches the response and observers get the domain forecast`() = runTest {
        val dao = InMemoryForecastDao()
        val repository = repository(dao, HttpStatusCode.OK, json)

        repository.observeForecast(limaLocation.id).test {
            assertNull(awaitItem())

            assertEquals(AppResult.Success(Unit), repository.refreshForecast(limaLocation))

            // replaceForecast writes current first, then the days: wait for the complete emission.
            var forecast = awaitItem()
            while (forecast?.daily?.size != 2) forecast = awaitItem()
            assertEquals(21.4, forecast.current.temperature.celsius)
            assertEquals(WeatherCondition.PARTLY_CLOUDY, forecast.current.condition)
            assertEquals(LocalDate.parse("2026-11-26"), forecast.daily[1].date)
            assertEquals(WeatherCondition.RAIN, forecast.daily[1].condition)
            assertEquals(42, forecast.updatedAtEpochMillis)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `failed refresh keeps the cache untouched`() = runTest {
        val dao = InMemoryForecastDao()
        repository(dao, HttpStatusCode.OK, json).refreshForecast(limaLocation)

        val result = repository(dao, HttpStatusCode.ServiceUnavailable, "{}").refreshForecast(limaLocation)

        assertEquals(AppResult.Failure(AppError.Http(503)), result)
        assertEquals(21.4, dao.current.value[limaLocation.id]?.temperatureCelsius)
    }
}
