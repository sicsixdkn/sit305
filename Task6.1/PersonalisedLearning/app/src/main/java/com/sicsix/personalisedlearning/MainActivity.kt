package com.sicsix.personalisedlearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sicsix.personalisedlearning.ui.screens.InterestsScreen
import com.sicsix.personalisedlearning.ui.screens.LoginScreen
import com.sicsix.personalisedlearning.ui.screens.QuizListScreen
import com.sicsix.personalisedlearning.ui.screens.QuizScreen
import com.sicsix.personalisedlearning.ui.screens.ResultsScreen
import com.sicsix.personalisedlearning.ui.screens.SignUpScreen
import com.sicsix.personalisedlearning.ui.theme.PersonalisedLearningTheme
import com.sicsix.personalisedlearning.viewmodels.QuizListViewModel
import com.sicsix.personalisedlearning.viewmodels.QuizViewModel
import com.sicsix.personalisedlearning.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalisedLearningTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PersonalisedLearningApp()
                }
            }
        }
    }
}

@Composable
fun PersonalisedLearningApp() {
    // Navigation controller to manage app navigation
    val navController = rememberNavController()

    // NavHost manages navigation within the app
    NavHost(navController = navController, startDestination = "login") {
        // Navigation graph definition
        composable("login") {
            val viewModel = hiltViewModel<UserViewModel>()
            LoginScreen(navController, viewModel)
        }
        composable("signup") {
            val viewModel = hiltViewModel<UserViewModel>()
            SignUpScreen(navController, viewModel)
        }
        composable("interests") {
            val viewModel = hiltViewModel<UserViewModel>()
            InterestsScreen(navController, viewModel)
        }
        composable("quizlist") {
            val viewModel = hiltViewModel<QuizListViewModel>()
            QuizListScreen(navController, viewModel)
        }
        composable("quiz/{quizId}") {
            val viewModel = hiltViewModel<QuizViewModel>()
            val quizId = it.arguments?.getString("quizId") ?: return@composable
            QuizScreen(navController, viewModel, quizId)
        }
        composable("results/{quizId}") {
            val viewModel = hiltViewModel<QuizViewModel>()
            val quizId = it.arguments?.getString("quizId") ?: return@composable
            ResultsScreen(navController, viewModel, quizId)
        }
    }
}









