package ai.data

import com.myapplication.common.data.AppDatabase
import com.myapplication.common.data.DailyScore
import com.myapplication.common.data.ExerciseLog
import com.myapplication.common.data.RoutineLog
import com.myapplication.common.data.SleepLog
import kotlinx.datetime.Clock

class Repository(private val database: AppDatabase) {

    fun insertRoutineLog(duration: Long) {
        database.appDatabaseQueries.insertRoutineLog(timestamp = Clock.System.now().toEpochMilliseconds(), duration = duration)
    }

    fun insertSleepLog(sleepTime: Long, wakeTime: Long, duration: Long) {
        database.appDatabaseQueries.insertSleepLog(sleepTime = sleepTime, wakeTime = wakeTime, duration = duration)
    }

    fun insertExerciseLog(activityType: String, duration: Long) {
        database.appDatabaseQueries.insertExerciseLog(activityType = activityType, duration = duration)
    }

    fun insertDailyScore(date: String, officeWorkScore: Long, personalProjectScore: Long) {
        database.appDatabaseQueries.insertDailyScore(date = date, officeWorkScore = officeWorkScore, personalProjectScore = personalProjectScore)
    }

    fun getRoutineLogs(): List<RoutineLog> = database.appDatabaseQueries.getRoutineLogs().executeAsList()

    fun getSleepLogs(): List<SleepLog> = database.appDatabaseQueries.getSleepLogs().executeAsList()

    fun getExerciseLogs(): List<ExerciseLog> = database.appDatabaseQueries.getExerciseLogs().executeAsList()

    fun getDailyScores(): List<DailyScore> = database.appDatabaseQueries.getDailyScores().executeAsList()
}