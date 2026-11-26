package com.navyblue.navyweather.feature.favorites.domain.usecase

import com.navyblue.navyweather.feature.favorites.domain.model.FavoriteCity
import com.navyblue.navyweather.feature.favorites.domain.repository.FavoritesRepository
import com.navyblue.navyweather.feature.favorites.fake.TestCities
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Uses a Mokkery mock: the use case only needs one stubbed call, so a fake would be overkill. */
class ObserveFavoritesUseCaseTest {

    private val repository = mock<FavoritesRepository>()
    private val useCase = ObserveFavoritesUseCase(repository)

    @Test
    fun `favorites are sorted alphabetically ignoring case`() = runTest {
        val lowercase = FavoriteCity(id = 1, name = "arequipa", country = "Peru", latitude = 0.0, longitude = 0.0)
        every { repository.observeFavorites() } returns flowOf(listOf(TestCities.madrid, TestCities.lima, lowercase))

        val names = useCase().first().map { it.name }

        assertEquals(listOf("arequipa", "Lima", "Madrid"), names)
    }

    @Test
    fun `accented names sort with their base letter`() = runTest {
        fun city(id: Long, name: String) = FavoriteCity(id = id, name = name, country = null, latitude = 0.0, longitude = 0.0)
        every { repository.observeFavorites() } returns flowOf(
            listOf(city(1, "Zaragoza"), city(2, "Ávila"), city(3, "Ñuñoa"), city(4, "Nazca"), city(5, "Oruro")),
        )

        val names = useCase().first().map { it.name }

        assertEquals(listOf("Ávila", "Nazca", "Ñuñoa", "Oruro", "Zaragoza"), names)
    }

    @Test
    fun `cities with the same name are ordered by country`() = runTest {
        val santiagoChile = FavoriteCity(id = 1, name = "Santiago", country = "Chile", latitude = 0.0, longitude = 0.0)
        val santiagoSpain = FavoriteCity(id = 2, name = "Santiago", country = "Spain", latitude = 0.0, longitude = 0.0)
        every { repository.observeFavorites() } returns flowOf(listOf(santiagoSpain, santiagoChile))

        val countries = useCase().first().map { it.country }

        assertEquals(listOf("Chile", "Spain"), countries)
    }
}
