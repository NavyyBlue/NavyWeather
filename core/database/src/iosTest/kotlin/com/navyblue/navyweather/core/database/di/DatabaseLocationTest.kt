package com.navyblue.navyweather.core.database.di

import kotlin.test.Test
import kotlin.test.assertTrue

/** On iOS the cache lives in Application Support, which the Files app does not show (specs/local-cache). */
class DatabaseLocationTest {

    @Test
    fun `database file is inside Application Support`() {
        val path = databaseFilePath()

        assertTrue("/Library/Application Support/" in path, path)
        assertTrue(path.endsWith("/navyweather.db"), path)
    }
}
