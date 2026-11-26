package com.navyblue.navyweather.feature.forecast.di

import com.navyblue.navyweather.feature.forecast.data.repository.FavoriteStatusRepositoryImpl
import com.navyblue.navyweather.feature.forecast.data.repository.ForecastRepositoryImpl
import com.navyblue.navyweather.feature.forecast.domain.repository.FavoriteStatusRepository
import com.navyblue.navyweather.feature.forecast.domain.repository.ForecastRepository
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveFavoriteStatusUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveForecastUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ObserveTemperatureUnitUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.RefreshForecastUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.SetTemperatureUnitUseCase
import com.navyblue.navyweather.feature.forecast.domain.usecase.ToggleFavoriteUseCase
import com.navyblue.navyweather.feature.forecast.presentation.ForecastViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val forecastModule = module {
    factory { ForecastRepositoryImpl(sync = get(), dao = get()) } bind ForecastRepository::class
    factory { FavoriteStatusRepositoryImpl(dao = get(), clock = get()) } bind FavoriteStatusRepository::class

    factoryOf(::ObserveForecastUseCase)
    factoryOf(::RefreshForecastUseCase)
    factoryOf(::ObserveFavoriteStatusUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::ObserveTemperatureUnitUseCase)
    factoryOf(::SetTemperatureUnitUseCase)

    // ForecastRoute comes from navigation through parametersOf(route).
    viewModelOf(::ForecastViewModel)
}
