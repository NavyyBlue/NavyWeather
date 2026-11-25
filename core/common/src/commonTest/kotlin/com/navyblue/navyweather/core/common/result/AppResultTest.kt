package com.navyblue.navyweather.core.common.result

import kotlin.test.Test
import kotlin.test.assertEquals

class AppResultTest {

    @Test
    fun `map transforms success`() {
        val result = AppResult.Success(2).map { it * 10 }
        assertEquals(AppResult.Success(20), result)
    }

    @Test
    fun `map keeps failure untouched`() {
        val failure: AppResult<Int> = AppResult.Failure(AppError.Network)
        assertEquals(failure, failure.map { it * 10 })
    }
}
