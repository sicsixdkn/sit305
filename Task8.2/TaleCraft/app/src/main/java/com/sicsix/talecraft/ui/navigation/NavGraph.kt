package com.sicsix.talecraft.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sicsix.talecraft.ui.screens.CreateStoryScreen
import com.sicsix.talecraft.ui.screens.CreateWorldScreen
import com.sicsix.talecraft.ui.screens.LibraryScreen
import com.sicsix.talecraft.ui.screens.LoginScreen
import com.sicsix.talecraft.ui.screens.SettingsScreen
import com.sicsix.talecraft.ui.screens.SignUpScreen
import com.sicsix.talecraft.ui.screens.StoryScreen
import com.sicsix.talecraft.ui.screens.WorldsScreen
import com.sicsix.talecraft.viewmodels.LibraryViewModel

@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues, libraryViewModel: LibraryViewModel? = null) {
    // NavHost manages navigation within the app
    NavHost(navController = navController, startDestination = Screen.Login.route, androidx.compose.ui.Modifier.padding(innerPadding)) {
        // Navigation graph definition
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Screen.Library.route) {
            if (libraryViewModel == null) {
                return@composable
            }
            LibraryScreen(navController, libraryViewModel)
        }
        composable(Screen.Worlds.route) {
            WorldsScreen(navController)
        }
        composable(Screen.Story.route) {
            StoryScreen()
        }
        composable(Screen.CreateWorld.route) {
            CreateWorldScreen(navController)
        }
        composable(Screen.CreateStory.route) {
            val worldId = it.arguments?.getString("worldId")?.toInt() ?: return@composable
            CreateStoryScreen(navController, worldId)
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}