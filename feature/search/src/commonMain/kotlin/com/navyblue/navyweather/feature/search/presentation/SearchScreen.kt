package com.navyblue.navyweather.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.navyblue.navyweather.core.designsystem.component.EmptyState
import com.navyblue.navyweather.core.designsystem.component.ErrorState
import com.navyblue.navyweather.core.designsystem.component.message
import com.navyblue.navyweather.core.designsystem.theme.Spacing
import com.navyblue.navyweather.core.mvi.CollectEffects
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.search.domain.model.CitySearchResult
import com.navyblue.navyweather.feature.search.resources.Res
import com.navyblue.navyweather.feature.search.resources.search_back
import com.navyblue.navyweather.feature.search.resources.search_clear
import com.navyblue.navyweather.feature.search.resources.search_hint_message
import com.navyblue.navyweather.feature.search.resources.search_hint_title
import com.navyblue.navyweather.feature.search.resources.search_no_results_message
import com.navyblue.navyweather.feature.search.resources.search_no_results_title
import com.navyblue.navyweather.feature.search.resources.search_placeholder
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SearchDestination(
    onNavigateToForecast: (ForecastRoute) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is SearchEffect.NavigateToForecast -> onNavigateToForecast(effect.route)
            SearchEffect.NavigateBack -> onNavigateBack()
        }
    }
    SearchScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = true,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier,
        // safeDrawing includes the keyboard (IME), so the content area shrinks above it.
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onIntent(SearchIntent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(Res.string.search_back))
                    }
                },
                title = {
                    TextField(
                        value = state.query,
                        onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                        placeholder = { Text(stringResource(Res.string.search_placeholder)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { onIntent(SearchIntent.QueryChanged("")) }) {
                                    Icon(Icons.Rounded.Close, contentDescription = stringResource(Res.string.search_clear))
                                }
                            }
                        },
                        // The app bar is the container; the field itself stays flat.
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                    )
                    // Requested from inside the top bar slot: Scaffold composes it separately, so an
                    // effect outside would run before the field is attached and focus would be lost.
                    if (autoFocus) {
                        LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    }
                },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
        when {
            state.error != null -> ErrorState(
                message = state.error.message(),
                onRetry = { onIntent(SearchIntent.RetryClicked) },
                modifier = contentModifier,
            )

            state.showNoResults -> EmptyState(
                icon = Icons.Rounded.Search,
                title = stringResource(Res.string.search_no_results_title),
                message = stringResource(Res.string.search_no_results_message, state.searchedQuery.orEmpty()),
                modifier = contentModifier,
            )

            state.results.isEmpty() && !state.isLoading -> EmptyState(
                icon = Icons.Rounded.Search,
                title = stringResource(Res.string.search_hint_title),
                message = stringResource(Res.string.search_hint_message),
                modifier = contentModifier,
            )

            else -> SearchResults(
                results = state.results,
                isLoading = state.isLoading,
                contentPadding = innerPadding,
                onCityClick = { onIntent(SearchIntent.CityClicked(it)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchResults(
    results: List<CitySearchResult>,
    isLoading: Boolean,
    contentPadding: PaddingValues,
    onCityClick: (CitySearchResult) -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(layoutDirection) + Spacing.screenHorizontal,
                end = contentPadding.calculateEndPadding(layoutDirection) + Spacing.screenHorizontal,
                top = contentPadding.calculateTopPadding() + Spacing.sm,
                bottom = contentPadding.calculateBottomPadding() + Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            if (isLoading) {
                item(key = "loading") {
                    Box(Modifier.fillMaxWidth().padding(Spacing.md), contentAlignment = Alignment.Center) {
                        LoadingIndicator()
                    }
                }
            }
            itemsIndexed(results, key = { _, city -> city.id }) { index, city ->
                SegmentedListItem(
                    onClick = { onCityClick(city) },
                    shapes = ListItemDefaults.segmentedShapes(index = index, count = results.size),
                    // Filled rows on the page surface, so the group reads as one unit.
                    colors = ListItemDefaults.segmentedColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    leadingContent = { Icon(Icons.Rounded.LocationOn, contentDescription = null) },
                    supportingContent = city.subtitle()?.let { subtitle -> { Text(subtitle) } },
                ) {
                    Text(city.name, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

/** "Lima, Peru" or just the country when there is no region. */
private fun CitySearchResult.subtitle(): String? =
    listOfNotNull(region?.takeIf { it != name }, country).joinToString(", ").ifEmpty { null }
