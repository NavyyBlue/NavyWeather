package com.navyblue.navyweather.feature.search.domain.usecase

import com.navyblue.navyweather.core.common.locale.LanguageProvider
import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult
import com.navyblue.navyweather.feature.search.domain.repository.SearchRepository

/**
 * Business rules for searching:
 * - Queries are trimmed, and anything shorter than [MIN_QUERY_LENGTH] returns no results
 *   without calling the network (one letter matches thousands of places).
 * - Results come in the user's language.
 */
class SearchCitiesUseCase(
    private val repository: SearchRepository,
    private val languageProvider: LanguageProvider,
) {

    suspend operator fun invoke(query: String): AppResult<List<CitySearchResult>> {
        val normalized = query.trim()
        if (normalized.length < MIN_QUERY_LENGTH) return AppResult.Success(emptyList())
        return repository.searchCities(normalized, languageProvider.languageCode())
    }

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}
