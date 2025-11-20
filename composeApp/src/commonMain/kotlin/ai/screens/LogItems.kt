package ai.screens

import ai.data.LogEntry
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun LogItem(log: LogEntry, onDelete: () -> Unit, onEdit: () -> Unit) {
    // Currently not using swipe to delete or edit in this basic item,
    // but the structure allows adding it. The parent list can handle swipe.
    // For now, just the display logic.

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() }, // Tap to Edit
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogIcon(log)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                LogContent(log)
            }
        }
    }
}

@Composable
fun LogIcon(log: LogEntry) {
    val icon = when (log) {
        is LogEntry.Routine -> Icons.Default.Timer
        is LogEntry.Sleep -> Icons.Default.Hotel
        is LogEntry.Exercise -> Icons.Default.DirectionsRun
        is LogEntry.DailyScore -> Icons.Default.Star
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(32.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun LogContent(log: LogEntry) {
    when (log) {
        is LogEntry.Routine -> {
            Text(text = log.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Duration: ${formatDuration(log.duration)}", style = MaterialTheme.typography.bodyMedium)
            if (!log.notes.isNullOrBlank()) {
                Text(text = log.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        is LogEntry.Sleep -> {
            val sleepTimeStr = formatTime(log.sleepTime)
            val wakeTimeStr = formatTime(log.wakeTime)
            val durationHours = log.duration / (1000 * 60 * 60.0)
            Text(text = "Sleep: $sleepTimeStr - $wakeTimeStr", style = MaterialTheme.typography.titleMedium)
            Text(text = "(${durationHours.toString().take(4)} hrs)", style = MaterialTheme.typography.bodyMedium)
            if (!log.description.isNullOrBlank()) {
                Text(text = log.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        is LogEntry.Exercise -> {
            Text(text = log.activityType, style = MaterialTheme.typography.titleMedium)
            Text(text = "${log.duration} mins", style = MaterialTheme.typography.bodyMedium)
            if (!log.description.isNullOrBlank()) {
                Text(text = log.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        is LogEntry.DailyScore -> {
            Text(text = "Scores", style = MaterialTheme.typography.titleMedium)
            Text(text = "Office: ${log.officeWorkScore}/10, Personal: ${log.personalProjectScore}/10", style = MaterialTheme.typography.bodyMedium)
            if (!log.reflection.isNullOrBlank()) {
                Text(text = log.reflection, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

fun formatDuration(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return if (hours > 0) {
        "${hours}h ${minutes}m ${seconds}s"
    } else {
        "${minutes}m ${seconds}s"
    }
}

fun formatTime(millis: Long): String {
    // This depends on how we store time. If it's epoch millis, we convert to local time.
    // If it's just millis from start of day, we format accordingly.
    // Assuming epoch millis for now as that's more standard for "Time Picker".
    // But wait, the previous implementation of parseTime in MainViewModel seemed to return duration from midnight?
    // "hours * 60 + minutes) * 60 * 1000"
    // If so, it's millis from midnight.

    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val amPm = if (hours >= 12) "PM" else "AM"
    val displayHour = if (hours > 12) hours - 12 else if (hours == 0L) 12 else hours
    return "${displayHour}:${minutes.toString().padStart(2, '0')} $amPm"
}
