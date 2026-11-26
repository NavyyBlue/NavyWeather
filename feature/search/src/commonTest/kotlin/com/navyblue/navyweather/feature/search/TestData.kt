package com.navyblue.navyweather.feature.search

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult
import com.navyblue.navyweather.feature.search.domain.repository.SearchRepository

val lima = CitySearchResult(3936456, "Lima", region = "Lima", country = "Peru", latitude = -12.04, longitude = -77.03)
val limaOhio = CitySearchResult(4517009, "Lima", region = "Ohio", country = "United States", latitude = 40.74, longitude = -84.1)

/** Records queries and answers with a configurable result. */
class FakeSearchRepository(var result: AppResult<List<CitySearchResult>> = AppResult.Success(listOf(lima, limaOhio))) :
    SearchRepository {
    val queries = mutableListOf<Pair<String, String>>()

    override suspend fun searchCities(query: String, languageCode: String): AppResult<List<CitySearchResult>> {
        queries += query to languageCode
        return result
    }
}
