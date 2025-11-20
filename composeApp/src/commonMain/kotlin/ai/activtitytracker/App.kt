package ai.activtitytracker

import ai.MainViewModel
import ai.data.DatabaseDriverFactory
import ai.data.Repository
import ai.screens.DailyScoreScreen
import ai.screens.ExerciseLogScreen
import ai.screens.SleepLogScreen
import ai.screens.StopwatchTrackerScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapplication.common.data.AppDatabase

@Composable
fun App(databaseDriverFactory: DatabaseDriverFactory) {
    val viewModel: MainViewModel = viewModel { 
        MainViewModel(Repository(AppDatabase(databaseDriverFactory.createDriver()))) 
    }

    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Stopwatch) }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(currentScreen) { screen ->
                    currentScreen = screen
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    is Screen.Stopwatch -> StopwatchTrackerScreen(
                        formattedTime = viewModel.formattedTime,
                        onStartClick = { viewModel.onStartClick() },
                        onPauseClick = { viewModel.onPauseClick() },
                        onResetClick = { viewModel.onResetClick() })
                    is Screen.SleepLog -> SleepLogScreen { sleepTime, wakeTime ->
                        viewModel.saveSleepLog(sleepTime, wakeTime)
                    }
                    is Screen.ExerciseLog -> ExerciseLogScreen { activityType, duration ->
                        viewModel.saveExerciseLog(activityType, duration)
                    }
                    is Screen.DailyScore -> DailyScoreScreen { officeWorkScore, personalProjectScore ->
                        viewModel.saveDailyScore(officeWorkScore, personalProjectScore)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Stopwatch") },
            label = { Text("Stopwatch") },
            selected = currentScreen == Screen.Stopwatch,
            onClick = { onScreenSelected(Screen.Stopwatch) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Sleep Log") },
            label = { Text("Sleep Log") },
            selected = currentScreen == Screen.SleepLog,
            onClick = { onScreenSelected(Screen.SleepLog) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Exercise Log") },
            label = { Text("Exercise Log") },
            selected = currentScreen == Screen.ExerciseLog,
            onClick = { onScreenSelected(Screen.ExerciseLog) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Daily Score") },
            label = { Text("Daily Score") },
            selected = currentScreen == Screen.DailyScore,
            onClick = { onScreenSelected(Screen.DailyScore) }
        )
    }
}

sealed class Screen(val route: String) {
    object Stopwatch : Screen("stopwatch")
    object SleepLog : Screen("sleep_log")
    object ExerciseLog : Screen("exercise_log")
    object DailyScore : Screen("daily_score")
}