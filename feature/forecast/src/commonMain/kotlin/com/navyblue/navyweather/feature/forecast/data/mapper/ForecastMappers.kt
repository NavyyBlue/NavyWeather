package com.navyblue.navyweather.feature.forecast.data.mapper

import com.navyblue.navyweather.core.database.entity.CurrentWeatherEntity
import com.navyblue.navyweather.core.database.entity.DailyForecastEntity
import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import com.navyblue.navyweather.feature.forecast.domain.model.CurrentWeather
import com.navyblue.navyweather.feature.forecast.domain.model.DailyForecast
import com.navyblue.navyweather.feature.forecast.domain.model.Forecast
import com.navyblue.navyweather.feature.forecast.domain.model.Location
import com.navyblue.navyweather.feature.forecast.domain.model.Temperature
import com.navyblue.navyweather.feature.forecast.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

internal fun toDomainForecast(current: CurrentWeatherEntity, daily: List<DailyForecastEntity>) = Forecast(
    current = CurrentWeather(
        temperature = Temperature(current.temperatureCelsius),
        feelsLike = Temperature(current.apparentTemperatureCelsius),
        relativeHumidity = current.relativeHumidity,
        windSpeedKmh = current.windSpeedKmh,
        condition = WeatherCondition.fromWmoCode(current.weatherCode),
        observedAt = LocalDateTime.parse(current.observedAt),
    ),
    daily = daily.map {
        DailyForecast(
            date = LocalDate.parse(it.date),
            condition = WeatherCondition.fromWmoCode(it.weatherCode),
            min = Temperature(it.minTemperatureCelsius),
            max = Temperature(it.maxTemperatureCelsius),
        )
    },
    updatedAtEpochMillis = current.fetchedAtEpochMillis,
)

internal fun Location.toFavoriteEntity(addedAtEpochMillis: Long) = FavoriteCityEntity(
    id = id,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
    addedAtEpochMillis = addedAtEpochMillis,
)
