package ai.screens

import ai.MainViewModel
import ai.data.LogEntry
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val logs by viewModel.logs.collectAsState()
    val weekendMode by viewModel.weekendMode.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf<InputType?>(null) }
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Logs") },
                    selected = currentScreen == Screen.Home,
                    onClick = { currentScreen = Screen.Home }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentScreen == Screen.Settings,
                    onClick = { currentScreen = Screen.Settings }
                )
            }
        },
        floatingActionButton = {
            if (currentScreen == Screen.Home) {
                FloatingActionButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Log")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(logs = logs, onDeleteLog = viewModel::deleteLog)
                Screen.Settings -> SettingsScreen(
                    weekendMode = weekendMode,
                    onWeekendModeChanged = viewModel::updateWeekendMode
                )
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add New Log", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))

                    ListItem(text = "🛌 Log Sleep", onClick = { showInputDialog = InputType.Sleep; showBottomSheet = false })
                    ListItem(text = "💪 Log Exercise", onClick = { showInputDialog = InputType.Exercise; showBottomSheet = false })
                    ListItem(text = "⭐ Daily Score", onClick = { showInputDialog = InputType.Score; showBottomSheet = false })
                    ListItem(text = "⏱️ Routine Timer", onClick = { showInputDialog = InputType.Routine; showBottomSheet = false })

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        showInputDialog?.let { type ->
            when (type) {
                InputType.Sleep -> SleepInputDialog(
                    onDismiss = { showInputDialog = null },
                    onSave = { sleep, wake, desc, date ->
                        viewModel.addSleepLog(sleep, wake, desc, date)
                        showInputDialog = null
                    }
                )
                InputType.Exercise -> ExerciseInputDialog(
                    onDismiss = { showInputDialog = null },
                    onSave = { type, dur, desc, date ->
                        viewModel.addExerciseLog(type, dur, desc, date)
                        showInputDialog = null
                    }
                )
                InputType.Score -> DailyScoreInputDialog(
                    weekendMode = weekendMode,
                    onDismiss = { showInputDialog = null },
                    onSave = { office, personal, refl, date ->
                        viewModel.addDailyScore(office, personal, refl, date)
                        showInputDialog = null
                    }
                )
                InputType.Routine -> RoutineInputDialog(
                    onDismiss = { showInputDialog = null },
                    onSave = { title, start, dur, notes, date ->
                        viewModel.addRoutineLog(title, start, dur, notes, date)
                        showInputDialog = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(logs: List<LogEntry>, onDeleteLog: (LogEntry) -> Unit) {
    if (logs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No logs yet. Tap + to add your first entry!", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        val groupedLogs = logs.groupBy { it.displayDate }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            groupedLogs.forEach { (date, logsForDate) ->
                stickyHeader {
                    DateHeader(date)
                }
                items(logsForDate, key = { it.id.toString() + it.logType }) { log ->
                    LogItem(
                        log = log,
                        onDelete = { onDeleteLog(log) },
                        onEdit = {
                            // TODO: Implement Edit logic
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun ListItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

enum class InputType {
    Sleep, Exercise, Score, Routine
}

enum class Screen {
    Home, Settings
}
