package ai.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.myapplication.common.data.AppDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // changed db name to force fresh creation since schema changed without migration
        return AndroidSqliteDriver(AppDatabase.Schema, context, "AppDatabase_v3.db")
    }
}
