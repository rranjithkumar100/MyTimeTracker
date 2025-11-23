package ai.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ai.data.WeekendMode
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DayOfWeek
import kotlin.time.ExperimentalTime

@Composable
fun SleepInputDialog(
    onDismiss: () -> Unit,
    onSave: (sleepTime: Long, wakeTime: Long, description: String?, date: String) -> Unit
) {
    var sleepTimeText by remember { mutableStateOf("") } // Format HH:MM
    var wakeTimeText by remember { mutableStateOf("") } // Format HH:MM
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDateString()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showSleepTimePicker by remember { mutableStateOf(false) }
    var showWakeTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    date = convertMillisToDate(millis)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showSleepTimePicker) {
        TimePickerModal(
            initialHour = parseTime(sleepTimeText)?.first ?: 22,
            initialMinute = parseTime(sleepTimeText)?.second ?: 30,
            onTimeSelected = { h, m ->
                sleepTimeText = formatTime(h, m)
                showSleepTimePicker = false
            },
            onDismiss = { showSleepTimePicker = false }
        )
    }

    if (showWakeTimePicker) {
        TimePickerModal(
            initialHour = parseTime(wakeTimeText)?.first ?: 7,
            initialMinute = parseTime(wakeTimeText)?.second ?: 0,
            onTimeSelected = { h, m ->
                wakeTimeText = formatTime(h, m)
                showWakeTimePicker = false
            },
            onDismiss = { showWakeTimePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Sleep") },
        text = {
            Column {
                ClickableTextField(
                    value = date,
                    label = "Date",
                    icon = Icons.Default.DateRange,
                    onClick = { showDatePicker = true }
                )
                ClickableTextField(
                    value = sleepTimeText,
                    label = "Sleep Time",
                    icon = Icons.Default.Schedule,
                    onClick = { showSleepTimePicker = true }
                )
                ClickableTextField(
                    value = wakeTimeText,
                    label = "Wake Time",
                    icon = Icons.Default.Schedule,
                    onClick = { showWakeTimePicker = true }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
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
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    date = convertMillisToDate(millis)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Exercise") },
        text = {
            Column {
                ClickableTextField(
                    value = date,
                    label = "Date",
                    icon = Icons.Default.DateRange,
                    onClick = { showDatePicker = true }
                )
                OutlinedTextField(
                    value = activityType,
                    onValueChange = { activityType = it },
                    label = { Text("Activity Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
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
    weekendMode: WeekendMode,
    onDismiss: () -> Unit,
    onSave: (officeScore: Long, personalScore: Long, reflection: String?, date: String) -> Unit
) {
    var officeScoreText by remember { mutableStateOf("") }
    var personalScoreText by remember { mutableStateOf("") }
    var reflection by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDateString()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isWeekend = remember(date, weekendMode) {
        try {
            val localDate = LocalDate.parse(date)
            val dayOfWeek = localDate.dayOfWeek
            when (weekendMode) {
                WeekendMode.FriSat -> dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY
                WeekendMode.SatSun -> dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
            }
        } catch (e: Exception) {
            false
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    date = convertMillisToDate(millis)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Score") },
        text = {
            Column {
                ClickableTextField(
                    value = date,
                    label = "Date",
                    icon = Icons.Default.DateRange,
                    onClick = { showDatePicker = true }
                )
                if (!isWeekend) {
                    OutlinedTextField(
                        value = officeScoreText,
                        onValueChange = { officeScoreText = it },
                        label = { Text("Office Score (1-10)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = personalScoreText,
                    onValueChange = { personalScoreText = it },
                    label = { Text("Personal Score (1-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reflection,
                    onValueChange = { reflection = it },
                    label = { Text("Reflection (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val office = if (isWeekend) 0L else officeScoreText.toLongOrNull()
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

@OptIn(ExperimentalTime::class)
@Composable
fun RoutineInputDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, startTime: Long, duration: Long, notes: String?, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDateString()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    date = convertMillisToDate(millis)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Routine") },
        text = {
            Column {
                ClickableTextField(
                    value = date,
                    label = "Date",
                    icon = Icons.Default.DateRange,
                    onClick = { showDatePicker = true }
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (minutes)") },
                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                     modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val duration = durationText.toLongOrNull()?.times(60000) // Convert to millis
                if (title.isNotBlank() && duration != null) {
                    val startTime = kotlin.time.Clock.System.now().toEpochMilliseconds() - duration
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

@Composable
fun ClickableTextField(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
     Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { Icon(icon, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}

@OptIn(ExperimentalTime::class)
fun getCurrentDateString(): String {
    val epochMillis = kotlin.time.Clock.System.now().toEpochMilliseconds()
    val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(epochMillis)
    val datetime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
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
