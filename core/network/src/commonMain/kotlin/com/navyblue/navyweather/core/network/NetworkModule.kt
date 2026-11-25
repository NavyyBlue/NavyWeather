package com.navyblue.navyweather.core.network

import io.ktor.client.plugins.logging.LogLevel
import org.koin.core.module.Module
import org.koin.dsl.module

/** @param enableHttpLogging pass `true` only for debug builds. */
fun networkModule(enableHttpLogging: Boolean): Module = module {
    single {
        HttpClientFactory.create(
            engine = platformHttpEngine(),
            logLevel = if (enableHttpLogging) LogLevel.INFO else LogLevel.NONE,
        )
    }
}
