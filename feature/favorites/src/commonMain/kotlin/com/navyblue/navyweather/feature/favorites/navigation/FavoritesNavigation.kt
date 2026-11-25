package com.navyblue.navyweather.feature.favorites.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.navyblue.navyweather.core.navigation.FavoritesRoute
import com.navyblue.navyweather.core.navigation.ForecastRoute
import com.navyblue.navyweather.feature.favorites.presentation.FavoritesDestination

/** The only public UI entry point of this feature. :shared calls it inside the NavHost. */
fun NavGraphBuilder.favoritesScreen(
    onNavigateToForecast: (ForecastRoute) -> Unit,
    onNavigateToSearch: (() -> Unit)?,
) {
    composable<FavoritesRoute> {
        FavoritesDestination(
            onNavigateToForecast = onNavigateToForecast,
            onNavigateToSearch = onNavigateToSearch,
        )
    }
}
