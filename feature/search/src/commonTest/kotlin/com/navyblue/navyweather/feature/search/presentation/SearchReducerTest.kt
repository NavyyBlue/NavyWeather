package com.navyblue.navyweather.feature.search.presentation

import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.feature.search.lima
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SearchReducerTest {

    private val withResults = SearchState(query = "Lima", results = listOf(lima), searchedQuery = "Lima")

    @Test
    fun `clearing the query resets to the idle hint`() {
        val state = SearchReducer.reduce(withResults, SearchIntent.QueryChanged("L"))

        assertEquals(SearchState(query = "L"), state)
    }

    @Test
    fun `typing keeps current results until new ones arrive`() {
        val state = SearchReducer.reduce(withResults, SearchIntent.QueryChanged("Lim"))

        assertEquals(listOf(lima), state.results)
        assertEquals("Lim", state.query)
    }

    @Test
    fun `empty results show the no-results state`() {
        val loading = SearchReducer.reduce(SearchState(query = "Xyzzy"), SearchIntent.SearchStarted)
        assertTrue(loading.isLoading)

        val state = SearchReducer.reduce(loading, SearchIntent.ResultsLoaded("Xyzzy", emptyList()))

        assertFalse(state.isLoading)
        assertTrue(state.showNoResults)
    }

    @Test
    fun `failure shows the error and clears results`() {
        val state = SearchReducer.reduce(withResults, SearchIntent.SearchFailed(AppError.Network))

        assertEquals(AppError.Network, state.error)
        assertTrue(state.results.isEmpty())
        assertFalse(state.showNoResults)
        assertNull(SearchReducer.reduce(state, SearchIntent.SearchStarted).error)
    }
}
