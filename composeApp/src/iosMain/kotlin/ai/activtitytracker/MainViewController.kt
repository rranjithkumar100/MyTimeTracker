package ai.activtitytracker

import ai.data.DatabaseDriverFactory
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { App(DatabaseDriverFactory()) }