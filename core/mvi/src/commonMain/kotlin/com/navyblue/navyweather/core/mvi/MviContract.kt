package com.navyblue.navyweather.core.mvi

/** Everything a screen renders. Must be an immutable `data class`. */
interface UiState

/** Everything that can happen on a screen: user actions and internal results. */
interface UiIntent

/** One-shot events that are not state: navigation, snackbars, dialogs. */
interface UiEffect

/**
 * Pure function that computes the next state. No coroutines, I/O or dependencies,
 * so it can be tested with plain assertions.
 */
fun interface Reducer<S : UiState, I : UiIntent> {
    fun reduce(state: S, intent: I): S
}
