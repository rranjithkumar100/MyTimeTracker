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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DailyScoreScreen(onSave: (officeWorkScore: String, personalProjectScore: String) -> Unit) {
    var officeWorkScore by remember { mutableStateOf("") }
    var personalProjectScore by remember { mutableStateOf("") }
    var showMotivationalMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Office Work is your primary income source", color = Color.Red)

        OutlinedTextField(
            value = officeWorkScore,
            onValueChange = { officeWorkScore = it },
            label = { Text("Office Work Score (1-10)") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = personalProjectScore,
            onValueChange = { personalProjectScore = it },
            label = { Text("Personal Project Score (1-10)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val officeScore = officeWorkScore.toIntOrNull() ?: 0
            val personalScore = personalProjectScore.toIntOrNull() ?: 0
            showMotivationalMessage = officeScore < 5 || personalScore < 5
            onSave(officeWorkScore, personalProjectScore)
        }) {
            Text("Save Daily Score")
        }

        if (showMotivationalMessage) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Don't give up! Tomorrow is a new day.", color = Color.Gray)
        }
    }
}