package com.navyblue.navyweather.feature.favorites.domain.usecase

import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository

/** Puts back a favorite that was just removed ("Undo"). */
class RestoreFavoriteUseCase(private val repository: FavoritesRepository) {

    suspend operator fun invoke(city: FavoriteCity) = repository.saveFavorite(city)
}
