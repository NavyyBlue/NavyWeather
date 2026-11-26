package com.navyblue.navyweather.feature.favorites.domain.usecase

import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository

/**
 * Removes a favorite and returns it, so the UI can offer "Undo" with [RestoreFavoriteUseCase].
 */
class RemoveFavoriteUseCase(private val repository: FavoritesRepository) {

    suspend operator fun invoke(city: FavoriteCity): FavoriteCity {
        repository.removeFavorite(city.id)
        return city
    }
}
