package com.navyblue.navyweather.feature.forecast.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.navyblue.navyweather.core.datastore.TemperatureUnit
import com.navyblue.navyweather.core.designsystem.component.ErrorState
import com.navyblue.navyweather.core.designsystem.component.LoadingState
import com.navyblue.navyweather.core.designsystem.component.loadMessage
import com.navyblue.navyweather.core.designsystem.component.message
import com.navyblue.navyweather.core.designsystem.theme.Spacing
import com.navyblue.navyweather.core.designsystem.theme.tabularNumbers
import com.navyblue.navyweather.core.mvi.CollectEffects
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.forecast.domain.model.DailyForecast
import com.navyblue.navyweather.feature.forecast.domain.model.Forecast
import com.navyblue.navyweather.feature.forecast.domain.model.Temperature
import com.navyblue.navyweather.feature.forecast.resources.Res
import com.navyblue.navyweather.feature.forecast.resources.day_fri
import com.navyblue.navyweather.feature.forecast.resources.day_mon
import com.navyblue.navyweather.feature.forecast.resources.day_sat
import com.navyblue.navyweather.feature.forecast.resources.day_sun
import com.navyblue.navyweather.feature.forecast.resources.day_thu
import com.navyblue.navyweather.feature.forecast.resources.day_tue
import com.navyblue.navyweather.feature.forecast.resources.day_wed
import com.navyblue.navyweather.feature.forecast.resources.forecast_add_favorite
import com.navyblue.navyweather.feature.forecast.resources.forecast_back
import com.navyblue.navyweather.feature.forecast.resources.forecast_celsius
import com.navyblue.navyweather.feature.forecast.resources.forecast_fahrenheit
import com.navyblue.navyweather.feature.forecast.resources.forecast_feels_like
import com.navyblue.navyweather.feature.forecast.resources.forecast_humidity
import com.navyblue.navyweather.feature.forecast.resources.forecast_max_min
import com.navyblue.navyweather.feature.forecast.resources.forecast_next_days
import com.navyblue.navyweather.feature.forecast.resources.forecast_remove_favorite
import com.navyblue.navyweather.feature.forecast.resources.forecast_today
import com.navyblue.navyweather.feature.forecast.resources.forecast_updated_at
import com.navyblue.navyweather.feature.forecast.resources.forecast_wind
import com.navyblue.navyweather.feature.forecast.resources.forecast_wind_value
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt
import kotlin.time.Instant

@Composable
internal fun ForecastDestination(
    route: ForecastRoute,
    onNavigateBack: () -> Unit,
    viewModel: ForecastViewModel = koinViewModel(key = "forecast-${route.cityId}") { parametersOf(route) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            ForecastEffect.NavigateBack -> onNavigateBack()
            is ForecastEffect.ShowRefreshError -> scope.launch {
                snackbarHostState.showSnackbar(effect.error.loadMessage())
            }
        }
    }

    ForecastScreen(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ForecastScreen(
    state: ForecastState,
    onIntent: (ForecastIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(state.location.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                subtitle = state.location.country?.let { country -> { Text(country) } },
                navigationIcon = {
                    IconButton(onClick = { onIntent(ForecastIntent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(Res.string.forecast_back))
                    }
                },
                actions = {
                    // The one app bar action, shown as a tonal toggle so its state reads at a glance.
                    FilledTonalIconToggleButton(
                        checked = state.isFavorite,
                        onCheckedChange = { onIntent(ForecastIntent.FavoriteToggled) },
                    ) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(
                                if (state.isFavorite) Res.string.forecast_remove_favorite else Res.string.forecast_add_favorite,
                            ),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            state.forecast != null -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(ForecastIntent.RefreshRequested) },
                modifier = Modifier.fillMaxSize(),
            ) {
                ForecastContent(
                    forecast = state.forecast,
                    unit = state.unit,
                    contentPadding = innerPadding,
                    onUnitSelected = { onIntent(ForecastIntent.UnitSelected(it)) },
                )
            }

            state.error != null -> ErrorState(
                message = state.error.message(),
                onRetry = { onIntent(ForecastIntent.RetryClicked) },
                modifier = Modifier.padding(innerPadding),
            )

            else -> LoadingState(Modifier.padding(innerPadding))
        }
    }
}

/**
 * Compact: one scrolling column. Expanded (840dp+): the extra width reveals the week next to
 * the current conditions instead of stretching the same column.
 */
@Composable
private fun ForecastContent(
    forecast: Forecast,
    unit: TemperatureUnit,
    contentPadding: PaddingValues,
    onUnitSelected: (TemperatureUnit) -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    val listPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection) + Spacing.screenHorizontal,
        end = contentPadding.calculateEndPadding(layoutDirection) + Spacing.screenHorizontal,
        top = contentPadding.calculateTopPadding() + Spacing.sm,
        bottom = contentPadding.calculateBottomPadding() + Spacing.xl,
    )

    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth >= 840.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                LazyColumn(Modifier.weight(1f).testTag(FORECAST_LIST_TAG), contentPadding = listPadding) {
                    currentConditions(forecast, unit, onUnitSelected)
                }
                LazyColumn(Modifier.weight(1f).testTag(FORECAST_DAYS_TAG), contentPadding = listPadding) {
                    dailyForecast(forecast, unit)
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                LazyColumn(
                    modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth().testTag(FORECAST_LIST_TAG),
                    contentPadding = listPadding,
                ) {
                    currentConditions(forecast, unit, onUnitSelected)
                    item { Spacer(Modifier.padding(top = Spacing.md)) }
                    dailyForecast(forecast, unit)
                }
            }
        }
    }
}

private fun LazyListScope.currentConditions(
    forecast: Forecast,
    unit: TemperatureUnit,
    onUnitSelected: (TemperatureUnit) -> Unit,
) {
    item(key = "hero") { HeroCard(forecast, unit) }
    item(key = "controls") {
        UpdatedAndUnitRow(forecast.updatedAtEpochMillis, unit, onUnitSelected, Modifier.padding(vertical = Spacing.md))
    }
    item(key = "details") { DetailTiles(forecast, unit) }
}

private fun LazyListScope.dailyForecast(forecast: Forecast, unit: TemperatureUnit) {
    item(key = "days-header") {
        Text(
            text = stringResource(Res.string.forecast_next_days),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.sm),
        )
    }
    itemsIndexed(forecast.daily, key = { _, day -> day.date.toString() }) { index, day ->
        DailyRow(
            day = day,
            isToday = day.date == forecast.current.observedAt.date,
            unit = unit,
            shape = segmentedShape(index, forecast.daily.size),
            modifier = Modifier.padding(bottom = 2.dp),
        )
    }
}

/**
 * The hero moment: the brand navy (`primaryContainer`, the one strong fill on the screen)
 * holds the current temperature, set in a huge tabular display number with its unit on the
 * same baseline.
 */
@Composable
private fun HeroCard(forecast: Forecast, unit: TemperatureUnit) {
    val current = forecast.current
    val conditionLabel = stringResource(current.condition.label)
    val temperature = current.temperature.roundedIn(unit)
    val unitSymbol = unit.symbol()
    Surface(
        modifier = Modifier.fillMaxWidth().testTag(HERO_CARD_TAG),
        shape = MaterialTheme.shapes.extraLargeIncreased,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Column(Modifier.padding(Spacing.xl), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f).semantics(mergeDescendants = true) {
                        contentDescription = "$temperature$unitSymbol, $conditionLabel"
                    },
                ) {
                    // Shrinks instead of wrapping when the user's font size makes 96sp too wide.
                    BasicText(
                        text = "$temperature",
                        style = MaterialTheme.typography.displayLarge.tabularNumbers().copy(
                            fontWeight = FontWeight.Medium,
                            color = LocalContentColor.current,
                        ),
                        maxLines = 1,
                        softWrap = false,
                        autoSize = TextAutoSize.StepBased(minFontSize = 40.sp, maxFontSize = 96.sp),
                        modifier = Modifier.weight(1f, fill = false).alignByBaseline().testTag(HERO_TEMPERATURE_TAG),
                    )
                    Text(
                        text = unitSymbol,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
                Text(
                    text = current.condition.glyph,
                    fontSize = 64.sp,
                    modifier = Modifier.clearAndSetSemantics { }, // Decorative: the label says it.
                )
            }
            Text(conditionLabel, style = MaterialTheme.typography.titleLarge)
            forecast.daily.firstOrNull()?.let { today ->
                Text(
                    text = stringResource(
                        Res.string.forecast_max_min,
                        "${today.max.roundedIn(unit)}°",
                        "${today.min.roundedIn(unit)}°",
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

/** When the data is from, and the °C/°F choice as a connected button group. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UpdatedAndUnitRow(
    updatedAtEpochMillis: Long,
    unit: TemperatureUnit,
    onUnitSelected: (TemperatureUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(Res.string.forecast_updated_at, updatedAtEpochMillis.toLocalTimeText()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        // Selection, not an action: secondary roles, so the hero keeps the only strong fill.
        val colors = ToggleButtonDefaults.toggleButtonColors(
            checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
            val celsius = stringResource(Res.string.forecast_celsius)
            val fahrenheit = stringResource(Res.string.forecast_fahrenheit)
            ToggleButton(
                checked = unit == TemperatureUnit.CELSIUS,
                onCheckedChange = { onUnitSelected(TemperatureUnit.CELSIUS) },
                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                colors = colors,
                modifier = Modifier.semantics { contentDescription = celsius },
            ) { Text("°C") }
            ToggleButton(
                checked = unit == TemperatureUnit.FAHRENHEIT,
                onCheckedChange = { onUnitSelected(TemperatureUnit.FAHRENHEIT) },
                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                colors = colors,
                modifier = Modifier.semantics { contentDescription = fahrenheit },
            ) { Text("°F") }
        }
    }
}

/** Equally important values for comparison: same shape, same size, evenly spaced. */
@Composable
private fun DetailTiles(forecast: Forecast, unit: TemperatureUnit) {
    val current = forecast.current
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        DetailTile(
            label = stringResource(Res.string.forecast_feels_like),
            value = "${current.feelsLike.roundedIn(unit)}°",
            modifier = Modifier.weight(1f),
        )
        DetailTile(
            label = stringResource(Res.string.forecast_humidity),
            value = "${current.relativeHumidity}%",
            modifier = Modifier.weight(1f),
        )
        DetailTile(
            label = stringResource(Res.string.forecast_wind),
            value = stringResource(Res.string.forecast_wind_value, current.windSpeedKmh.roundToInt()),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            Modifier.padding(horizontal = Spacing.md, vertical = Spacing.md).semantics(mergeDescendants = true) {},
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge.tabularNumbers(), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

/** A day of the week: highs carry the warm tertiary accent, lows stay quiet. */
@Composable
private fun DailyRow(
    day: DailyForecast,
    isToday: Boolean,
    unit: TemperatureUnit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    val dayName = if (isToday) stringResource(Res.string.forecast_today) else stringResource(day.date.dayOfWeek.shortName())
    val condition = stringResource(day.condition.label)
    Surface(modifier = modifier.fillMaxWidth(), shape = shape, color = MaterialTheme.colorScheme.surfaceContainer) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md).semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(72.dp),
            )
            Text(day.condition.glyph, fontSize = 24.sp, modifier = Modifier.clearAndSetSemantics { contentDescription = condition })
            Spacer(Modifier.weight(1f))
            TemperatureText(day.min, unit, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(Spacing.lg))
            TemperatureText(day.max, unit, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun TemperatureText(temperature: Temperature, unit: TemperatureUnit, color: androidx.compose.ui.graphics.Color) {
    Text(
        text = "${temperature.roundedIn(unit)}°",
        style = MaterialTheme.typography.titleMedium.tabularNumbers(),
        color = color,
        modifier = Modifier.width(40.dp),
    )
}

/** Grouped rows: large outer corners, small inner ones (Material segmented list shape). */
private fun segmentedShape(index: Int, count: Int): RoundedCornerShape {
    val outer = 16.dp
    val inner = 4.dp
    return when {
        count == 1 -> RoundedCornerShape(outer)
        index == 0 -> RoundedCornerShape(topStart = outer, topEnd = outer, bottomStart = inner, bottomEnd = inner)
        index == count - 1 -> RoundedCornerShape(topStart = inner, topEnd = inner, bottomStart = outer, bottomEnd = outer)
        else -> RoundedCornerShape(inner)
    }
}

private fun TemperatureUnit.symbol(): String = when (this) {
    TemperatureUnit.CELSIUS -> "°C"
    TemperatureUnit.FAHRENHEIT -> "°F"
}

private fun DayOfWeek.shortName(): StringResource = when (this) {
    DayOfWeek.MONDAY -> Res.string.day_mon
    DayOfWeek.TUESDAY -> Res.string.day_tue
    DayOfWeek.WEDNESDAY -> Res.string.day_wed
    DayOfWeek.THURSDAY -> Res.string.day_thu
    DayOfWeek.FRIDAY -> Res.string.day_fri
    DayOfWeek.SATURDAY -> Res.string.day_sat
    DayOfWeek.SUNDAY -> Res.string.day_sun
}

/** "14:05" in the device's time zone (when the data was downloaded on this device). */
private fun Long.toLocalTimeText(): String {
    val time = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).time
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}

internal const val FORECAST_LIST_TAG = "forecast_list"
internal const val HERO_TEMPERATURE_TAG = "hero_temperature"
internal const val HERO_CARD_TAG = "hero_card"
internal const val FORECAST_DAYS_TAG = "forecast_days"
