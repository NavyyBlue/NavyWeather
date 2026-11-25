package com.navyblue.navyweather.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI store on top of the multiplatform [ViewModel].
 *
 * Flow for every [onIntent]:
 *  1. [reducer] computes the new [state] (pure, synchronous).
 *  2. [handleIntent] runs side effects (use cases) with the state already updated.
 *     Side-effect results come back as new intents, so every state change goes
 *     through the reducer and is traceable.
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S,
    private val reducer: Reducer<S, I>,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    fun onIntent(intent: I) {
        _state.update { reducer.reduce(it, intent) }
        handleIntent(intent)
    }

    /** Side effects for the given intent. Default: none. */
    protected open fun handleIntent(intent: I) = Unit

    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
