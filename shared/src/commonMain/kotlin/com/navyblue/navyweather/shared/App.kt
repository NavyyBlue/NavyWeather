package com.navyblue.navyweather.shared

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.navyblue.navyweather.core.designsystem.theme.NavyWeatherTheme
import com.navyblue.navyweather.core.navigation.FavoritesRoute
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.core.navigation.SearchRoute
import com.navyblue.navyweather.feature.favorites.navigation.favoritesScreen
import com.navyblue.navyweather.shared.placeholder.PlaceholderScreen
import com.navyblue.navyweather.shared.resources.Res
import com.navyblue.navyweather.shared.resources.placeholder_search_title
import org.jetbrains.compose.resources.stringResource

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

            // Temporary destinations until the search and forecast features exist (Phase 4).
            composable<SearchRoute> {
                PlaceholderScreen(
                    title = stringResource(Res.string.placeholder_search_title),
                    onNavigateBack = { navController.popBackStack() },
                )
            }
            composable<ForecastRoute> { entry ->
                val route = entry.toRoute<ForecastRoute>()
                PlaceholderScreen(
                    title = listOfNotNull(route.name, route.country).joinToString(", "),
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }
    }
}
