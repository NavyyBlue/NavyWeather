package com.navyblue.navyweather.feature.search.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.semantics.SemanticsProperties
import com.navyblue.navyweather.core.common.result.AppError
import com.navyblue.navyweather.core.designsystem.component.ERROR_STATE_RETRY_TAG
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult
import com.navyblue.navyweather.feature.search.lima
import com.navyblue.navyweather.feature.search.limaOhio
import com.navyblue.navyweather.feature.search.resources.Res
import com.navyblue.navyweather.feature.search.resources.search_hint_message
import com.navyblue.navyweather.feature.search.resources.search_no_results_message
import com.navyblue.navyweather.feature.search.resources.search_no_results_title
import org.jetbrains.compose.resources.stringResource
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * City search screen (specs/city-search). Runs on the JVM and the iOS simulator; expected
 * texts are read from the same string resources, so the test passes in any locale.
 */
@OptIn(ExperimentalTestApi::class)
class SearchScreenTest {

    private val isEditable = SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText)

    @Test
    fun opensWithAFocusedEmptyFieldAndTheIdleHint() = runComposeUiTest {
        lateinit var hint: String
        setContent {
            hint = stringResource(Res.string.search_hint_message)
            NavyWeatherTheme { SearchScreen(state = SearchState(), onIntent = {}) }
        }

        // Focus is requested from a LaunchedEffect after the first frame.
        waitUntil(timeoutMillis = 3_000) { runCatching { onNode(isEditable).assertIsFocused() }.isSuccess }
        onNodeWithText(hint).assertIsDisplayed()
    }

    @Test
    fun resultRowsShowRegionAndCountryWithoutRepeatingTheName() = runComposeUiTest {
        val madrid = CitySearchResult(3117735, "Madrid", region = "Madrid", country = "Spain", latitude = 40.42, longitude = -3.7)
        val intents = mutableListOf<SearchIntent>()
        setContent {
            NavyWeatherTheme {
                SearchScreen(
                    state = SearchState(query = "Li", results = listOf(limaOhio, madrid, lima.copy(region = null)), searchedQuery = "Li"),
                    onIntent = { intents += it },
                    autoFocus = false,
                )
            }
        }

        onNodeWithText("Ohio, United States").assertIsDisplayed()
        onNodeWithText("Spain").assertIsDisplayed() // Region "Madrid" equals the name, so only the country.
        onNodeWithText("Peru").assertIsDisplayed() // No region at all.

        onNodeWithText("Spain").performClick()
        assertEquals(listOf<SearchIntent>(SearchIntent.CityClicked(madrid)), intents)
    }

    @Test
    fun noResultsRepeatsTheQuery() = runComposeUiTest {
        lateinit var title: String
        lateinit var message: String
        setContent {
            title = stringResource(Res.string.search_no_results_title)
            message = stringResource(Res.string.search_no_results_message, "Xyzzy")
            NavyWeatherTheme {
                SearchScreen(state = SearchState(query = "Xyzzy", searchedQuery = "Xyzzy"), onIntent = {}, autoFocus = false)
            }
        }

        onNodeWithText(title).assertIsDisplayed()
        onNodeWithText(message).assertIsDisplayed()
    }

    @Test
    fun errorStateRetries() = runComposeUiTest {
        val intents = mutableListOf<SearchIntent>()
        setContent {
            NavyWeatherTheme {
                SearchScreen(state = SearchState(query = "Lima", error = AppError.Network), onIntent = { intents += it }, autoFocus = false)
            }
        }

        onNodeWithTag(ERROR_STATE_RETRY_TAG).performClick()

        assertEquals(listOf<SearchIntent>(SearchIntent.RetryClicked), intents)
    }
}
