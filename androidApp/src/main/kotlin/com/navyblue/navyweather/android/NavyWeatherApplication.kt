package com.navyblue.navyweather.android

import android.app.Application
import com.navyblue.navyweather.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class NavyWeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(enableHttpLogging = BuildConfig.DEBUG) {
            androidContext(this@NavyWeatherApplication)
        }
    }
}
