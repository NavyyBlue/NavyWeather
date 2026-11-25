package com.navyblue.navyweather.feature.favorites.di

import com.navyblue.navyweather.feature.favorites.data.repository.FavoritesRepositoryImpl
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.ObserveTemperatureUnitUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RefreshFavoritesWeatherUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RemoveFavoriteUseCase
import com.navyblue.navyweather.feature.favorites.domain.usecase.RestoreFavoriteUseCase
import com.navyblue.navyweather.feature.favorites.presentation.FavoritesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val favoritesModule = module {
    factory { FavoritesRepositoryImpl(dao = get(), forecastSync = get(), clock = get()) } bind FavoritesRepository::class
    factoryOf(::ObserveFavoritesUseCase)
    factoryOf(::RemoveFavoriteUseCase)
    factoryOf(::RestoreFavoriteUseCase)
    factoryOf(::RefreshFavoritesWeatherUseCase)
    factoryOf(::ObserveTemperatureUnitUseCase)
    viewModelOf(::FavoritesViewModel)
}
