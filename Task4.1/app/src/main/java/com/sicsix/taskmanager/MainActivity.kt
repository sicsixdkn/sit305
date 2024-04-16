package com.sicsix.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sicsix.taskmanager.ui.screens.MainScreen
import com.sicsix.taskmanager.ui.theme.TaskManagerTheme

/**
 * Main activity for the Task Manager application.
 *
 * This is the entry point of the application where the Compose UI is set up.
 * The activity extends ComponentActivity, which provides the base functionality for Compose integration.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // MainScreen is the root composable function that hosts different screens and navigation of the app.
                    MainScreen()
                }
            }
        }
    }
}