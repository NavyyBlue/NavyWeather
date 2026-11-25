package com.navyblue.navyweather.core.mvi

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.navyblue.navyweather.core.testing.runViewModelTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals

/** Verifies the MVI contract every screen relies on (specs/presentation-architecture). */
class MviViewModelTest {

    private data class CounterState(val count: Int = 0) : UiState

    private sealed interface CounterIntent : UiIntent {
        data object Increment : CounterIntent
        data object Announce : CounterIntent
        data object Load : CounterIntent
        data class Loaded(val count: Int) : CounterIntent
    }

    private data class Announced(val count: Int) : UiEffect

    private val counterReducer = Reducer<CounterState, CounterIntent> { state, intent ->
        when (intent) {
            CounterIntent.Increment -> state.copy(count = state.count + 1)
            CounterIntent.Announce -> state
            CounterIntent.Load -> state
            is CounterIntent.Loaded -> state.copy(count = intent.count)
        }
    }

    private inner class CounterViewModel : MviViewModel<CounterState, CounterIntent, Announced>(
        initialState = CounterState(),
        reducer = counterReducer,
    ) {
        val stateSeenBySideEffects = mutableListOf<Int>()

        override fun handleIntent(intent: CounterIntent) {
            when (intent) {
                CounterIntent.Increment -> stateSeenBySideEffects += currentState.count
                CounterIntent.Announce -> sendEffect(Announced(currentState.count))
                CounterIntent.Load -> viewModelScope.launch {
                    delay(100) // Simulated background work.
                    onIntent(CounterIntent.Loaded(count = 42))
                }
                is CounterIntent.Loaded -> Unit
            }
        }
    }

    @Test
    fun `current state is available immediately and each change is a new value`() = runViewModelTest {
        val viewModel = CounterViewModel()

        viewModel.state.test {
            assertEquals(CounterState(0), awaitItem())
            viewModel.onIntent(CounterIntent.Increment)
            assertEquals(CounterState(1), awaitItem())
        }
    }

    @Test
    fun `background result comes back as an intent through the reducer`() = runViewModelTest {
        val viewModel = CounterViewModel()

        viewModel.state.test {
            assertEquals(CounterState(0), awaitItem())
            viewModel.onIntent(CounterIntent.Load)
            assertEquals(CounterState(42), awaitItem())
        }
    }

    @Test
    fun `reducer is deterministic`() {
        val state = CounterState(3)

        assertEquals(
            counterReducer.reduce(state, CounterIntent.Increment),
            counterReducer.reduce(state, CounterIntent.Increment),
        )
        assertEquals(CounterState(3), state) // The input is not mutated.
    }

    @Test
    fun `side effects run after the state update`() = runViewModelTest {
        val viewModel = CounterViewModel()

        viewModel.onIntent(CounterIntent.Increment)
        viewModel.onIntent(CounterIntent.Increment)

        assertEquals(listOf(1, 2), viewModel.stateSeenBySideEffects)
    }

    @Test
    fun `effect sent without observers is delivered once when observing starts`() = runViewModelTest {
        val viewModel = CounterViewModel()
        viewModel.onIntent(CounterIntent.Increment)
        viewModel.onIntent(CounterIntent.Announce)

        viewModel.effects.test {
            assertEquals(Announced(1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // A new observer (e.g. the UI recreated after a configuration change) does not get it again.
        viewModel.effects.test {
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `effects keep the order they were sent in`() = runViewModelTest {
        val viewModel = CounterViewModel()

        viewModel.effects.test {
            viewModel.onIntent(CounterIntent.Announce)
            viewModel.onIntent(CounterIntent.Increment)
            viewModel.onIntent(CounterIntent.Announce)

            assertEquals(Announced(0), awaitItem())
            assertEquals(Announced(1), awaitItem())
        }
    }
}
