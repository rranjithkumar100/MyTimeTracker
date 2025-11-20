package ai.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SleepInputDialog(
    onDismiss: () -> Unit,
    onSave: (sleepTime: Long, wakeTime: Long, description: String?, date: String) -> Unit
) {
    var sleepTimeText by remember { mutableStateOf("") } // Format HH:MM
    var wakeTimeText by remember { mutableStateOf("") } // Format HH:MM
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDateString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Sleep") },
        text = {
            Column {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") }
                )
                OutlinedTextField(
                    value = sleepTimeText,
                    onValueChange = { sleepTimeText = it },
                    label = { Text("Sleep Time (HH:MM)") }
                )
                OutlinedTextField(
                    value = wakeTimeText,
                    onValueChange = { wakeTimeText = it },
                    label = { Text("Wake Time (HH:MM)") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val sleep = parseTimeInput(sleepTimeText)
                val wake = parseTimeInput(wakeTimeText)
                if (sleep != null && wake != null) {
                    onSave(sleep, wake, description, date)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExerciseInputDialog(
    onDismiss: () -> Unit,
    onSave: (activityType: String, duration: Long, description: String?, date: String) -> Unit
) {
    var activityType by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDateString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Exercise") },
        text = {
            Column {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") }
                )
                OutlinedTextField(
                    value = activityType,
                    onValueChange = { activityType = it },
                    label = { Text("Activity Type") }
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val duration = durationText.toLongOrNull()
                if (activityType.isNotBlank() && duration != null) {
                    onSave(activityType, duration, description, date)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DailyScoreInputDialog(
    onDismiss: () -> Unit,
    onSave: (officeScore: Long, personalScore: Long, reflection: String?, date: String) -> Unit
) {
    var officeScoreText by remember { mutableStateOf("") }
    var personalScoreText by remember { mutableStateOf("") }
    var reflection by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDateString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Score") },
        text = {
            Column {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") }
                )
                OutlinedTextField(
                    value = officeScoreText,
                    onValueChange = { officeScoreText = it },
                    label = { Text("Office Score (1-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = personalScoreText,
                    onValueChange = { personalScoreText = it },
                    label = { Text("Personal Score (1-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = reflection,
                    onValueChange = { reflection = it },
                    label = { Text("Reflection (Optional)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val office = officeScoreText.toLongOrNull()
                val personal = personalScoreText.toLongOrNull()
                if (office != null && personal != null) {
                    onSave(office, personal, reflection, date)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RoutineInputDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, startTime: Long, duration: Long, notes: String?, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDateString()) }
    // Simplified: Manual entry for now, timer logic can be added later or user inputs duration

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Routine") },
        text = {
            Column {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") }
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Title") }
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (minutes)") },
                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val duration = durationText.toLongOrNull()?.times(60000) // Convert to millis
                if (title.isNotBlank() && duration != null) {
                    // Start time: just use current time for now or add input
                    val startTime = Clock.System.now().toEpochMilliseconds() - duration
                    onSave(title, startTime, duration, notes, date)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


fun getCurrentDateString(): String {
    val currentMoment = Clock.System.now()
    val datetime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = datetime.monthNumber.toString().padStart(2, '0')
    val day = datetime.dayOfMonth.toString().padStart(2, '0')
    return "${datetime.year}-$month-$day"
}

fun parseTimeInput(time: String): Long? {
    // Input format HH:MM. Returns millis from start of day (or just millis representation)
    return try {
        val parts = time.split(":")
        val hours = parts[0].toLong()
        val minutes = parts[1].toLong()
        (hours * 3600 + minutes * 60) * 1000
    } catch (e: Exception) {
        null
    }
}
