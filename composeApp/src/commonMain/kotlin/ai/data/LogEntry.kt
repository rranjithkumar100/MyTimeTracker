package ai.data

sealed class LogEntry {
    abstract val id: Long
    abstract val displayDate: String
    abstract val logType: String

    data class Routine(
        override val id: Long,
        val title: String,
        val startTime: Long,
        val duration: Long,
        val notes: String?,
        override val displayDate: String,
        override val logType: String = "ROUTINE"
    ) : LogEntry()

    data class Sleep(
        override val id: Long,
        val sleepTime: Long,
        val wakeTime: Long,
        val duration: Long,
        val description: String?,
        override val displayDate: String,
        override val logType: String = "SLEEP"
    ) : LogEntry()

    data class Exercise(
        override val id: Long,
        val activityType: String,
        val duration: Long,
        val description: String?,
        override val displayDate: String,
        override val logType: String = "EXERCISE"
    ) : LogEntry()

    data class DailyScore(
        override val id: Long,
        val officeWorkScore: Long,
        val personalProjectScore: Long,
        val reflection: String?,
        override val displayDate: String,
        override val logType: String = "SCORE"
    ) : LogEntry()
}
