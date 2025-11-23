package ai.activtitytracker

import ai.MainViewModel
import ai.data.DatabaseDriverFactory
import ai.data.Repository
import ai.health.HealthServiceFactory
import ai.screens.MainScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapplication.common.data.AppDatabase

@Composable
fun App(databaseDriverFactory: DatabaseDriverFactory, healthServiceFactory: HealthServiceFactory) {
    val viewModel: MainViewModel = viewModel { 
        MainViewModel(
            Repository(AppDatabase(databaseDriverFactory.createDriver())),
            healthServiceFactory.create()
        )
    }

    MaterialTheme {
        MainScreen(viewModel)
    }
}
