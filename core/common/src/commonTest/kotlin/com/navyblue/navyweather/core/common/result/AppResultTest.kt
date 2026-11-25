package com.navyblue.navyweather.core.common.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AppResultTest {

    @Test
    fun `map transforms success`() {
        val result = AppResult.Success(2).map { it * 10 }
        assertEquals(AppResult.Success(20), result)
    }

    @Test
    fun `map keeps failure untouched`() {
        val failure: AppResult<Int> = AppResult.Failure(AppError.Network)
        var mapperCalled = false

        val result = failure.map { mapperCalled = true; it * 10 }

        assertEquals(failure, result)
        assertFalse(mapperCalled)
    }

    @Test
    fun `server error keeps its status code`() {
        val failure: AppResult<Int> = AppResult.Failure(AppError.Http(503))

        val error = (failure as AppResult.Failure).error

        assertEquals(AppError.Http(503), error)
        assertEquals(503, (error as AppError.Http).code)
    }
}
