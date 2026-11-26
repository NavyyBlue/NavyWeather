package com.navyblue.navyweather.feature.search.domain

import com.navyblue.navyweather.core.common.result.AppResult
import com.navyblue.navyweather.feature.search.domain.repository.SearchRepository
import com.navyblue.navyweather.feature.search.domain.usecase.SearchCitiesUseCase
import com.navyblue.navyweather.feature.search.lima
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchCitiesUseCaseTest {

    private val repository = mock<SearchRepository>()
    private val useCase = SearchCitiesUseCase(repository) { "es" }

    @Test
    fun `short queries return nothing without calling the network`() = runTest {
        val result = useCase(" L ")

        assertEquals(AppResult.Success(emptyList()), result)
        verifySuspend(not) { repository.searchCities(any(), any()) }
    }

    @Test
    fun `query is trimmed and sent in the device language`() = runTest {
        everySuspend { repository.searchCities("Lima", "es") } returns AppResult.Success(listOf(lima))

        val result = useCase("  Lima ")

        assertEquals(AppResult.Success(listOf(lima)), result)
        verifySuspend { repository.searchCities("Lima", "es") }
    }
}
