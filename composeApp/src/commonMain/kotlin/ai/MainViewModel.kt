package ai

import ai.data.Repository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant as KxInstant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.ExperimentalTime
import kotlin.time.Clock as KtClock

class MainViewModel(private val repository: Repository) : ViewModel() {

    // Stopwatch
    private val _formattedTime = MutableStateFlow("00:00:00")
    val formattedTime: StateFlow<String> = _formattedTime

    private var stopwatchJob: Job? = null
    private var elapsedTime = 0L

    @OptIn(ExperimentalTime::class)
    fun onStartClick() {
        if (stopwatchJob?.isActive == true) return
        stopwatchJob = viewModelScope.launch {
            // get epoch millis from kotlin.time.Clock.System
            val startTime = KtClock.System.now().toEpochMilliseconds() - elapsedTime
            while (true) {
                elapsedTime = KtClock.System.now().toEpochMilliseconds() - startTime
                _formattedTime.value = formatTime(elapsedTime)
                delay(1000)
            }
        }
    }

    fun onPauseClick() {
        stopwatchJob?.cancel()
    }

    fun onResetClick() {
        stopwatchJob?.cancel()
        repository.insertRoutineLog(elapsedTime)
        elapsedTime = 0L
        _formattedTime.value = formatTime(elapsedTime)
    }

    @OptIn(ExperimentalTime::class)
    private fun formatTime(timeInMillis: Long): String {
        // Use kotlinx.datetime.Instant to convert epoch millis -> local time components
        val instant = KxInstant.fromEpochMilliseconds(timeInMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
        val hours = localDateTime.hour.toString().padStart(2, '0')
        val minutes = localDateTime.minute.toString().padStart(2, '0')
        val seconds = localDateTime.second.toString().padStart(2, '0')
        return "$hours:$minutes:$seconds"
    }

    // Sleep Log
    fun saveSleepLog(sleepTime: String, wakeTime: String) {
        val sleepTimeMillis = parseTime(sleepTime)
        val wakeTimeMillis = parseTime(wakeTime)

        if (sleepTimeMillis != null && wakeTimeMillis != null) {
            val duration = wakeTimeMillis - sleepTimeMillis
            repository.insertSleepLog(sleepTimeMillis, wakeTimeMillis, duration)
        }
    }

    // Exercise Log
    fun saveExerciseLog(activityType: String, duration: String) {
        val durationMinutes = duration.toLongOrNull()
        if (durationMinutes != null) {
            repository.insertExerciseLog(activityType, durationMinutes)
        }
    }

    // Daily Score
    @OptIn(ExperimentalTime::class)
    fun saveDailyScore(officeWorkScore: String, personalProjectScore: String) {
        val officeScore = officeWorkScore.toLongOrNull()
        val personalScore = personalProjectScore.toLongOrNull()

        val currentDate = KxInstant.fromEpochMilliseconds(
            KtClock.System.now().toEpochMilliseconds()
        )
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        if (officeScore != null && personalScore != null) {
            repository.insertDailyScore(currentDate.toString(), officeScore, personalScore)
        }
    }


    private fun parseTime(time: String): Long? {
        return try {
            val parts = time.split(":")
            val hours = parts[0].toLong()
            val minutes = parts[1].toLong()
            (hours * 60 + minutes) * 60 * 1000
        } catch (e: Exception) {
            null
        }
    }
}
