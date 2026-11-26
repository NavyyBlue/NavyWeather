package com.navyblue.navyweather.feature.favorites.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.designsystem.component.EmptyState
import com.navyblue.navyweather.core.designsystem.component.LoadingState
import com.navyblue.navyweather.core.designsystem.component.loadMessage
import com.navyblue.navyweather.core.designsystem.theme.Spacing
import com.navyblue.navyweather.core.designsystem.theme.tabularNumbers
import com.navyblue.navyweather.core.mvi.CollectEffects
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.resources.Res
import com.navyblue.navyweather.feature.favorites.resources.favorites_empty_message
import com.navyblue.navyweather.feature.favorites.resources.favorites_empty_title
import com.navyblue.navyweather.feature.favorites.resources.favorites_remove_city
import com.navyblue.navyweather.feature.favorites.resources.favorites_removed
import com.navyblue.navyweather.feature.favorites.resources.favorites_search_city
import com.navyblue.navyweather.feature.favorites.resources.favorites_subtitle
import com.navyblue.navyweather.feature.favorites.resources.favorites_temperature_unavailable
import com.navyblue.navyweather.feature.favorites.resources.favorites_title
import com.navyblue.navyweather.feature.favorites.resources.favorites_undo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stateful entry point: owns the ViewModel and turns effects into navigation and snackbars.
 *
 * @param onNavigateToSearch `null` hides the search entry points (the search feature is optional).
 */
@Composable
internal fun FavoritesDestination(
    onNavigateToForecast: (ForecastRoute) -> Unit,
    onNavigateToSearch: (() -> Unit)?,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is FavoritesEffect.NavigateToForecast -> onNavigateToForecast(effect.route)
            FavoritesEffect.NavigateToSearch -> onNavigateToSearch?.invoke()
            // Own coroutine: showSnackbar suspends until dismissed, and must not block the
            // collector (a tap on another city would otherwise wait for the snackbar).
            is FavoritesEffect.ShowRemoved -> scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = getString(Res.string.favorites_removed, effect.city.name),
                    actionLabel = getString(Res.string.favorites_undo),
                    duration = SnackbarDuration.Short,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onIntent(FavoritesIntent.UndoRemoveClicked(effect.city))
                }
            }
            is FavoritesEffect.ShowRefreshError -> scope.launch {
                snackbarHostState.showSnackbar(effect.error.loadMessage())
            }
        }
    }

    FavoritesScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
        showSearch = onNavigateToSearch != null,
    )
}

/** Stateless screen: renders [state] and reports user actions as intents. Used by UI tests. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FavoritesScreen(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    showSearch: Boolean = true,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.favorites_title)) },
                subtitle = { Text(stringResource(Res.string.favorites_subtitle)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            // The empty state already has its own search button; one primary action per screen.
            // Small extended FAB: Material's standard 56dp size; the list, not the FAB, leads here.
            if (showSearch && state.cities.isNotEmpty()) {
                SmallExtendedFloatingActionButton(
                    onClick = { onIntent(FavoritesIntent.SearchClicked) },
                    icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    text = { Text(stringResource(Res.string.favorites_search_city)) },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(innerPadding))

            state.cities.isEmpty() -> EmptyState(
                icon = Icons.Rounded.LocationOn,
                title = stringResource(Res.string.favorites_empty_title),
                message = stringResource(Res.string.favorites_empty_message),
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                action = if (showSearch) {
                    {
                        Button(onClick = { onIntent(FavoritesIntent.SearchClicked) }) {
                            Text(stringResource(Res.string.favorites_search_city))
                        }
                    }
                } else {
                    null
                },
            )

            else -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(FavoritesIntent.RefreshRequested) },
                modifier = Modifier.fillMaxSize(),
            ) {
                FavoritesList(
                    cities = state.cities,
                    unit = state.unit,
                    contentPadding = innerPadding,
                    onIntent = onIntent,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FavoritesList(
    cities: List<FavoriteCity>,
    unit: TemperatureUnit,
    contentPadding: PaddingValues,
    onIntent: (FavoritesIntent) -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            // Readable width on tablets and desktop windows; full width on phones.
            modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth().testTag(FAVORITES_LIST_TAG),
            // All system insets (including side ones in landscape) go to contentPadding so rows
            // scroll behind the bars (edge-to-edge), plus room so the FAB never covers the last row.
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(layoutDirection) + Spacing.screenHorizontal,
                end = contentPadding.calculateEndPadding(layoutDirection) + Spacing.screenHorizontal,
                top = contentPadding.calculateTopPadding() + Spacing.sm,
                bottom = contentPadding.calculateBottomPadding() + FAB_CLEARANCE,
            ),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            itemsIndexed(cities, key = { _, city -> city.id }) { index, city ->
                FavoriteRow(
                    city = city,
                    unit = unit,
                    index = index,
                    count = cities.size,
                    onClick = { onIntent(FavoritesIntent.CityClicked(city)) },
                    onRemove = { onIntent(FavoritesIntent.RemoveClicked(city)) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

/**
 * A saved place and its current temperature, the thing people glance at.
 * Removing is a swipe (with Undo); screen readers get the same action as a custom action.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FavoriteRow(
    city: FavoriteCity,
    unit: TemperatureUnit,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shapes = ListItemDefaults.segmentedShapes(index = index, count = count)
    val removeLabel = stringResource(Res.string.favorites_remove_city, city.name)
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        onDismiss = { value -> if (value == SwipeToDismissBoxValue.EndToStart) onRemove() },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, shapes.shape)
                    .padding(horizontal = Spacing.xl),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
    ) {
        SegmentedListItem(
            onClick = onClick,
            shapes = shapes,
            // The focusable row carries the action, so screen readers can remove without swiping.
            modifier = Modifier.semantics {
                customActions = listOf(CustomAccessibilityAction(removeLabel) { onRemove(); true })
            },
            // Filled rows on the page surface, so the group reads as one unit.
            colors = ListItemDefaults.segmentedColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            leadingContent = { Icon(Icons.Rounded.LocationOn, contentDescription = null) },
            supportingContent = city.country?.let { country -> { Text(country) } },
            trailingContent = {
                val temperature = city.temperatureCelsius
                Text(
                    text = if (temperature != null) "${unit.fromCelsius(temperature)}°" else "–",
                    style = MaterialTheme.typography.headlineSmall.tabularNumbers(),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = if (temperature == null) {
                        val unavailable = stringResource(Res.string.favorites_temperature_unavailable)
                        Modifier.semantics { this.contentDescription = unavailable }
                    } else {
                        Modifier
                    },
                )
            },
        ) {
            Text(city.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

internal const val FAVORITES_LIST_TAG = "favorites_list"
private val FAB_CLEARANCE = 88.dp
