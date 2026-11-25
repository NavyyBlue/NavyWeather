package com.navyblue.navyweather.core.testing

import com.navyblue.navyweather.core.common.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(dispatcher: CoroutineDispatcher) : DispatcherProvider {
    override val main: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}
