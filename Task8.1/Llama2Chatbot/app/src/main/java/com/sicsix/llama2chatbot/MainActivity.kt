package com.sicsix.llama2chatbot

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
import com.sicsix.llama2chatbot.ui.screens.ChatScreen
import com.sicsix.llama2chatbot.ui.screens.LoginScreen
import com.sicsix.llama2chatbot.ui.theme.Llama2ChatbotTheme
import com.sicsix.llama2chatbot.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Llama2ChatbotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Llama2ChatbotApp()
                }
            }
        }
    }
}

@Composable
fun Llama2ChatbotApp() {
    // Navigation controller to manage app navigation
    val navController = rememberNavController()

    // NavHost manages navigation within the app
    NavHost(navController = navController, startDestination = "login") {
        // Navigation graph definition
        composable("login") {
            val viewModel = hiltViewModel<AppViewModel>()
            LoginScreen(navController, viewModel)
        }
        composable("chat") {
            val viewModel = hiltViewModel<AppViewModel>()
            ChatScreen(viewModel)
        }
    }
}









