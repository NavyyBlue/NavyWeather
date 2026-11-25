package com.navyblue.navyweather.core.database

import androidx.room.RoomDatabase
import com.navyblue.navyweather.core.database.dao.FavoriteCityDao
import com.navyblue.navyweather.core.database.dao.ForecastDao
import com.navyblue.navyweather.core.database.di.databaseModule
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

class DatabaseModuleTest {

    @Test
    fun `the module provides both DAOs`() {
        val koin = koinApplication {
            allowOverride(true)
            modules(
                databaseModule,
                // The platform file location is replaced with an in-memory database for the test.
                module { single<RoomDatabase.Builder<AppDatabase>> { inMemoryDatabaseBuilder() } },
            )
        }.koin

        try {
            assertNotNull(koin.get<FavoriteCityDao>())
            assertNotNull(koin.get<ForecastDao>())
        } finally {
            koin.get<AppDatabase>().close()
            koin.close()
        }
    }
}
