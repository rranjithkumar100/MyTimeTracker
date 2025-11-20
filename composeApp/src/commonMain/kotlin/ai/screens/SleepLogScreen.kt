package ai.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SleepLogScreen(onSave: (sleepTime: String, wakeTime: String) -> Unit) {
    var sleepTime by remember { mutableStateOf("") }
    var wakeTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = sleepTime,
            onValueChange = { sleepTime = it },
            label = { Text("Sleep Time (e.g., 22:00)") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = wakeTime,
            onValueChange = { wakeTime = it },
            label = { Text("Wake Time (e.g., 06:00)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSave(sleepTime, wakeTime) }) {
            Text("Save Sleep Log")
        }
    }
}