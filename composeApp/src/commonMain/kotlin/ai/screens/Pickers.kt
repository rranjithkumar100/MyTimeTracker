package ai.screens

import androidx.compose.material3.AlertDialog
// import androidx.compose.material3.DatePicker
// import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
// import androidx.compose.material3.TimePicker
// import androidx.compose.material3.rememberDatePickerState
// import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    // Placeholder for compatibility testing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Date Picker") },
        text = { Text("Date Picker is temporarily disabled for compatibility check.") },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(null) // Or current date
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Placeholder for compatibility testing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Time Picker") },
        text = { Text("Time Picker is temporarily disabled for compatibility check.") },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(initialHour, initialMinute)
                onDismiss()
            }) {
                Text("OK")
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
fun convertMillisToDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val date = instant.toLocalDateTime(TimeZone.UTC).date
    return date.toString() // YYYY-MM-DD
}

@OptIn(ExperimentalTime::class)
fun convertDateToMillis(dateStr: String): Long? {
    return try {
        val date = LocalDate.parse(dateStr)
        date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    } catch (e: Exception) {
        null
    }
}

fun formatTime(hour: Int, minute: Int): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

fun parseTime(timeStr: String): Pair<Int, Int>? {
    return try {
        val parts = timeStr.split(":")
        Pair(parts[0].toInt(), parts[1].toInt())
    } catch (e: Exception) {
        null
    }
}
