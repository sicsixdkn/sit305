package com.sicsix.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sicsix.quizapp.ui.screens.MainScreen
import com.sicsix.quizapp.ui.screens.QuizScreen
import com.sicsix.quizapp.ui.screens.ResultScreen
import com.sicsix.quizapp.ui.theme.QuizAppTheme
import com.sicsix.quizapp.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation controller to manage app navigation
                    val navController = rememberNavController()

                    // View model to share data between screens
                    val quizViewModel = remember { QuizViewModel() }

                    // NavHost manages navigation within the app
                    NavHost(navController = navController, startDestination = "mainScreen") {
                        // Navigation graph definition
                        composable("mainScreen") { MainScreen(navController, quizViewModel) }
                        composable("quizScreen") { QuizScreen(navController, quizViewModel) }
                        composable("resultScreen") { ResultScreen(navController, quizViewModel) }
                    }
                }
            }
        }
    }
}