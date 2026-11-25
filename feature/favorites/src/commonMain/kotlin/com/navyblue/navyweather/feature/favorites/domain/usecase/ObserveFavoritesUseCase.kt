package com.navyblue.navyweather.feature.favorites.domain.usecase

import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Favorites in the order the user scans them: alphabetical by city name, then by country,
 * ignoring case and accents ("Ávila" sorts with "A"). The order is a product rule, so it
 * lives here and not in the DAO query.
 */
class ObserveFavoritesUseCase(private val repository: FavoritesRepository) {

    operator fun invoke(): Flow<List<FavoriteCity>> = repository.observeFavorites().map { cities ->
        cities.sortedWith(
            compareBy<FavoriteCity> { it.name.sortKey() }.thenBy { it.country.orEmpty().sortKey() },
        )
    }
}

/**
 * Case- and accent-insensitive key for Latin names, without a platform collator.
 * "ñ" maps to "n{" so it sorts after every other "n" word, as in Spanish ('{' follows 'z').
 */
internal fun String.sortKey(): String = buildString {
    for (char in this@sortKey.lowercase()) {
        append(ACCENT_FOLDING[char] ?: char.toString())
    }
}

private val ACCENT_FOLDING: Map<Char, String> = buildMap {
    "áàâäãå".forEach { put(it, "a") }
    "éèêë".forEach { put(it, "e") }
    "íìîï".forEach { put(it, "i") }
    "óòôöõø".forEach { put(it, "o") }
    "úùûü".forEach { put(it, "u") }
    put('ç', "c")
    put('ñ', "n{")
}
