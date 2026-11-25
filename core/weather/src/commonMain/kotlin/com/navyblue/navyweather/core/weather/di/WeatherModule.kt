package com.navyblue.navyweather.core.weather.di

import com.navyblue.navyweather.core.weather.ForecastSync
import com.navyblue.navyweather.core.weather.OpenMeteoForecastApi
import org.koin.dsl.module

val weatherModule = module {
    factory { OpenMeteoForecastApi(client = get()) }
    factory { ForecastSync(api = get(), dao = get(), clock = get()) }
}
