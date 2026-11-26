package com.navyblue.navyweather.feature.favorites.data.mapper

import com.navyblue.navyweather.core.database.entity.FavoriteCityEntity
import com.navyblue.navyweather.core.database.entity.FavoriteCityWithWeather
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity

internal fun FavoriteCityEntity.toDomain() = FavoriteCity(
    id = id,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
)

internal fun FavoriteCityWithWeather.toDomain() = city.toDomain().copy(
    temperatureCelsius = temperatureCelsius,
    weatherUpdatedAtEpochMillis = weatherFetchedAtEpochMillis,
)

internal fun FavoriteCity.toEntity(addedAtEpochMillis: Long) = FavoriteCityEntity(
    id = id,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
    addedAtEpochMillis = addedAtEpochMillis,
)
