package com.navyblue.navyweather.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.core.navigation.SearchRoute
import com.navyblue.navyweather.feature.search.presentation.SearchDestination

fun NavGraphBuilder.searchScreen(
    onNavigateToForecast: (ForecastRoute) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<SearchRoute> {
        SearchDestination(onNavigateToForecast = onNavigateToForecast, onNavigateBack = onNavigateBack)
    }
}
