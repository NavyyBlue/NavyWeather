package com.navyblue.navyweather.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Runs a coroutine test with `Dispatchers.Main` replaced, so `viewModelScope`
 * uses virtual time. Multiplatform alternative to a JUnit `MainDispatcherRule`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun runViewModelTest(
    dispatcher: TestDispatcher = StandardTestDispatcher(),
    testBody: suspend TestScope.() -> Unit,
) {
    Dispatchers.setMain(dispatcher)
    try {
        runTest(dispatcher) { testBody() }
    } finally {
        Dispatchers.resetMain()
    }
}
