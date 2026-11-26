package com.navyblue.navyweather.feature.search.domain.model

data class CitySearchResult(
    val id: Long,
    val name: String,
    /** First-level administrative area (state, region, department). */
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
)
