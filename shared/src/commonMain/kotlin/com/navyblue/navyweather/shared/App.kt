package com.navyblue.navyweather.shared

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.core.navigation.FavoritesRoute
import com.navyblue.navyweather.core.navigation.SearchRoute
import com.navyblue.navyweather.feature.favorites.navigation.favoritesScreen
import com.navyblue.navyweather.feature.forecast.navigation.forecastScreen
import com.navyblue.navyweather.feature.search.navigation.searchScreen

/**
 * Root composable shared by Android and iOS. The only place that knows every feature:
 * it wires their destinations together through the routes in core:navigation (ADR-0014).
 */
@Composable
fun App() {
    NavyWeatherTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = FavoritesRoute) {
            favoritesScreen(
                onNavigateToForecast = { route -> navController.navigate(route) },
                onNavigateToSearch = { navController.navigate(SearchRoute) },
            )
            searchScreen(
                // Replace search with the forecast, so Back from the forecast returns to favorites.
                onNavigateToForecast = { route ->
                    navController.navigate(route) { popUpTo<SearchRoute> { inclusive = true } }
                },
                onNavigateBack = { navController.popBackStack() },
            )
            forecastScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
