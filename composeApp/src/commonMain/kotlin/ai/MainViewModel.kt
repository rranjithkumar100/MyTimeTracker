package ai

import ai.data.LogEntry
import ai.data.Repository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs

    init {
        refreshLogs()
    }

    fun refreshLogs() {
        viewModelScope.launch {
            val routineLogs = repository.getRoutineLogs().map {
                LogEntry.Routine(it.id, it.title, it.startTime, it.duration, it.notes, it.display_date)
            }
            val sleepLogs = repository.getSleepLogs().map {
                LogEntry.Sleep(it.id, it.sleepTime, it.wakeTime, it.duration, it.description, it.display_date)
            }
            val exerciseLogs = repository.getExerciseLogs().map {
                LogEntry.Exercise(it.id, it.activityType, it.duration, it.description, it.display_date)
            }
            val dailyScores = repository.getDailyScores().map {
                LogEntry.DailyScore(it.id, it.officeWorkScore, it.personalProjectScore, it.reflection, it.display_date)
            }

            val allLogs = (routineLogs + sleepLogs + exerciseLogs + dailyScores).sortedByDescending {
                // Sorting by display_date descending, then maybe by id or another field
                // Assuming display_date is YYYY-MM-DD or similar comparable string
                it.displayDate
            }
            _logs.value = allLogs
        }
    }

    fun addRoutineLog(title: String, startTime: Long, duration: Long, notes: String?, displayDate: String) {
        viewModelScope.launch {
            repository.insertRoutineLog(title, startTime, duration, notes, displayDate)
            refreshLogs()
        }
    }

    fun addSleepLog(sleepTime: Long, wakeTime: Long, description: String?, displayDate: String) {
        viewModelScope.launch {
            val duration = wakeTime - sleepTime // Simple duration calculation
            repository.insertSleepLog(sleepTime, wakeTime, duration, description, displayDate)
            refreshLogs()
        }
    }

    fun addExerciseLog(activityType: String, duration: Long, description: String?, displayDate: String) {
        viewModelScope.launch {
            repository.insertExerciseLog(activityType, duration, description, displayDate)
            refreshLogs()
        }
    }

    fun addDailyScore(officeWorkScore: Long, personalProjectScore: Long, reflection: String?, displayDate: String) {
        viewModelScope.launch {
            repository.insertDailyScore(officeWorkScore, personalProjectScore, reflection, displayDate)
            refreshLogs()
        }
    }

    fun deleteLog(logEntry: LogEntry) {
        viewModelScope.launch {
            when (logEntry) {
                is LogEntry.Routine -> repository.deleteRoutineLog(logEntry.id)
                is LogEntry.Sleep -> repository.deleteSleepLog(logEntry.id)
                is LogEntry.Exercise -> repository.deleteExerciseLog(logEntry.id)
                is LogEntry.DailyScore -> repository.deleteDailyScore(logEntry.id)
            }
            refreshLogs()
        }
    }

}
