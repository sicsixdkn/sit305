package com.sicsix.itube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sicsix.itube.ui.screens.HomeScreen
import com.sicsix.itube.ui.screens.LoginScreen
import com.sicsix.itube.ui.screens.PlayScreen
import com.sicsix.itube.ui.screens.PlaylistScreen
import com.sicsix.itube.ui.screens.SignUpScreen
import com.sicsix.itube.ui.theme.ITubeTheme
import com.sicsix.itube.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ITubeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ITubeApp()
                }
            }
        }
    }
}

@Composable
fun ITubeApp(viewModel: UserViewModel = viewModel()) {
    // Navigation controller to manage app navigation
    val navController = rememberNavController()

    // NavHost manages navigation within the app
    NavHost(navController = navController, startDestination = "login") {
        // Navigation graph definition
        composable("login") {
            LoginScreen(navController, viewModel)
        }
        composable("signup") {
            SignUpScreen(navController, viewModel)
        }
        composable("home") {
            HomeScreen(navController, viewModel)
        }
        composable("playlist") {
            PlaylistScreen(navController, viewModel)
        }
        composable("play/{videoId}") { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable
            PlayScreen(videoId = videoId)
        }

    }
}









