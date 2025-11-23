package ai.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.myapplication.common.data.AppDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // Force rebuild
        return NativeSqliteDriver(AppDatabase.Schema, "AppDatabase_v3.db")
    }
}
