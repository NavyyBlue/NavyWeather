package com.navyblue.navyweather.feature.search.di

import com.navyblue.navyweather.feature.search.data.remote.GeocodingApi
import com.navyblue.navyweather.feature.search.data.repository.SearchRepositoryImpl
import com.navyblue.navyweather.feature.search.domain.repository.SearchRepository
import com.navyblue.navyweather.feature.search.domain.usecase.SearchCitiesUseCase
import com.navyblue.navyweather.feature.search.presentation.SearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val searchModule = module {
    factory { GeocodingApi(client = get()) }
    factory { SearchRepositoryImpl(api = get()) } bind SearchRepository::class
    factoryOf(::SearchCitiesUseCase)
    viewModelOf(::SearchViewModel)
}
