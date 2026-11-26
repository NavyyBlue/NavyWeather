package com.navyblue.navyweather.feature.search.data.repository

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.core.network.safeApiCall
import com.navyblue.navyweather.feature.search.data.remote.GeocodingApi
import com.navyblue.navyweather.feature.search.data.remote.GeocodingResultDto
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult
import com.navyblue.navyweather.feature.search.domain.repository.SearchRepository

internal class SearchRepositoryImpl(private val api: GeocodingApi) : SearchRepository {

    override suspend fun searchCities(query: String, languageCode: String): AppResult<List<CitySearchResult>> =
        safeApiCall { api.search(query, languageCode).results.map { it.toDomain() } }
}

internal fun GeocodingResultDto.toDomain() = CitySearchResult(
    id = id,
    name = name,
    region = region,
    country = country,
    latitude = latitude,
    longitude = longitude,
)
