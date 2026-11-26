package com.navyblue.navyweather.feature.forecast.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.forecast.presentation.ForecastDestination

fun NavGraphBuilder.forecastScreen(onNavigateBack: () -> Unit) {
    composable<ForecastRoute> { entry ->
        ForecastDestination(route = entry.toRoute<ForecastRoute>(), onNavigateBack = onNavigateBack)
    }
}
