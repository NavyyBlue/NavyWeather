package com.navyblue.navyweather.feature.search.domain.repository

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult

interface SearchRepository {
    /** @param languageCode ISO 639-1 code; place names come back in that language. */
    suspend fun searchCities(query: String, languageCode: String): AppResult<List<CitySearchResult>>
}
