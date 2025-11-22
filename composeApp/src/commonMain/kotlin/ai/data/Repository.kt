package ai.data

import com.myapplication.common.data.AppDatabase
import com.myapplication.common.data.DailyScore
import com.myapplication.common.data.ExerciseLog
import com.myapplication.common.data.RoutineLog
import com.myapplication.common.data.SleepLog

class Repository(private val database: AppDatabase) {

    fun insertRoutineLog(title: String, startTime: Long, duration: Long, notes: String?, displayDate: String) {
        database.appDatabaseQueries.insertRoutineLog(
            title = title,
            startTime = startTime,
            duration = duration,
            notes = notes,
            display_date = displayDate,
            log_type = "ROUTINE"
        )
    }

    fun insertSleepLog(sleepTime: Long, wakeTime: Long, duration: Long, description: String?, displayDate: String) {
        database.appDatabaseQueries.insertSleepLog(
            sleepTime = sleepTime,
            wakeTime = wakeTime,
            duration = duration,
            description = description,
            display_date = displayDate,
            log_type = "SLEEP"
        )
    }

    fun insertExerciseLog(activityType: String, duration: Long, description: String?, displayDate: String) {
        database.appDatabaseQueries.insertExerciseLog(
            activityType = activityType,
            duration = duration,
            description = description,
            display_date = displayDate,
            log_type = "EXERCISE"
        )
    }

    fun insertDailyScore(officeWorkScore: Long, personalProjectScore: Long, reflection: String?, displayDate: String) {
        database.appDatabaseQueries.insertDailyScore(
            officeWorkScore = officeWorkScore,
            personalProjectScore = personalProjectScore,
            reflection = reflection,
            display_date = displayDate,
            log_type = "SCORE"
        )
    }

    fun getRoutineLogs(): List<RoutineLog> = database.appDatabaseQueries.getRoutineLogs().executeAsList()

    fun getSleepLogs(): List<SleepLog> = database.appDatabaseQueries.getSleepLogs().executeAsList()

    fun getExerciseLogs(): List<ExerciseLog> = database.appDatabaseQueries.getExerciseLogs().executeAsList()

    fun getDailyScores(): List<DailyScore> = database.appDatabaseQueries.getDailyScores().executeAsList()

    fun deleteRoutineLog(id: Long) = database.appDatabaseQueries.deleteRoutineLog(id)
    fun deleteSleepLog(id: Long) = database.appDatabaseQueries.deleteSleepLog(id)
    fun deleteExerciseLog(id: Long) = database.appDatabaseQueries.deleteExerciseLog(id)
    fun deleteDailyScore(id: Long) = database.appDatabaseQueries.deleteDailyScore(id)

    fun saveSetting(key: String, value: String) {
        database.appDatabaseQueries.insertOrReplaceSetting(key, value)
    }

    fun getSetting(key: String): String? {
        return database.appDatabaseQueries.getSetting(key).executeAsOneOrNull()
    }
}
