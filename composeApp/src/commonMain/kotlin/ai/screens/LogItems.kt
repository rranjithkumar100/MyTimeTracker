package ai.screens

import ai.data.LogEntry
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LogItem(log: LogEntry, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() },
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
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
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
            Row {
                if (log.officeWorkScore > 0) {
                    Text(text = "Office: ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${log.officeWorkScore}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = getScoreColor(log.officeWorkScore)
                    )
                    Text(text = ", ", style = MaterialTheme.typography.bodyMedium)
                }
                Text(text = "Personal: ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${log.personalProjectScore}/10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = getScoreColor(log.personalProjectScore)
                )
            }
            if (!log.reflection.isNullOrBlank()) {
                Text(text = log.reflection, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

fun getScoreColor(score: Long): Color {
    return when {
        score > 7 -> Color.Green
        score >= 4 -> Color.Yellow
        else -> Color.Red
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
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val amPm = if (hours >= 12) "PM" else "AM"
    val displayHour = if (hours > 12) hours - 12 else if (hours == 0L) 12 else hours
    return "${displayHour}:${minutes.toString().padStart(2, '0')} $amPm"
}
