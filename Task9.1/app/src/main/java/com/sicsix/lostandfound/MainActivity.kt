package com.sicsix.lostandfound

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
import com.sicsix.lostandfound.ui.screens.AdvertScreen
import com.sicsix.lostandfound.ui.screens.AdvertsListScreen
import com.sicsix.lostandfound.ui.screens.CreateAdvertScreen
import com.sicsix.lostandfound.ui.screens.HomeScreen
import com.sicsix.lostandfound.ui.screens.MapScreen
import com.sicsix.lostandfound.ui.theme.LostAndFoundTheme
import com.sicsix.lostandfound.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LostAndFoundTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LostAndFoundApp()
                }
            }
        }
    }
}

@Composable
fun LostAndFoundApp() {
    // Navigation controller to manage app navigation
    val navController = rememberNavController()

    // NavHost manages navigation within the app
    NavHost(navController = navController, startDestination = "home") {
        // Navigation graph definition
        composable("home") {
            HomeScreen(navController)
        }
        composable("createAdvert") {
            val viewModel = hiltViewModel<AppViewModel>()
            CreateAdvertScreen(navController, viewModel)
        }
        composable("advertsList") {
            val viewModel = hiltViewModel<AppViewModel>()
            AdvertsListScreen(navController, viewModel)
        }
        composable("advert/{advertId}") {
            val viewModel = hiltViewModel<AppViewModel>()
            val advertId = it.arguments?.getString("advertId") ?: return@composable
            AdvertScreen(viewModel, advertId)
        }
        composable("map") {
            val viewModel = hiltViewModel<AppViewModel>()
            MapScreen(navController, viewModel)
        }
    }
}









