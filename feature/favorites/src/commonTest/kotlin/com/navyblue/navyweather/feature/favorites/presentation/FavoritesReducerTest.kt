package com.navyblue.navyweather.feature.favorites.presentation

import com.navyblue.navyweather.feature.favorites.fake.TestCities
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

/** The reducer is a pure function: plain input -> output assertions, no coroutines or mocks. */
class FavoritesReducerTest {

    @Test
    fun `loaded favorites replace the list and stop loading`() {
        val state = FavoritesReducer.reduce(FavoritesState(), FavoritesIntent.FavoritesLoaded(listOf(TestCities.lima)))

        assertEquals(listOf(TestCities.lima), state.cities)
        assertFalse(state.isLoading)
    }

    @Test
    fun `remove hides the city immediately`() {
        val loaded = FavoritesState(cities = listOf(TestCities.lima, TestCities.madrid), isLoading = false)

        val state = FavoritesReducer.reduce(loaded, FavoritesIntent.RemoveClicked(TestCities.lima))

        assertEquals(listOf(TestCities.madrid), state.cities)
    }

    @Test
    fun `navigation intents do not change the state`() {
        val loaded = FavoritesState(cities = listOf(TestCities.lima), isLoading = false)

        assertSame(loaded, FavoritesReducer.reduce(loaded, FavoritesIntent.SearchClicked))
        assertSame(loaded, FavoritesReducer.reduce(loaded, FavoritesIntent.CityClicked(TestCities.lima)))
    }
}
