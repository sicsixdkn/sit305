package com.sicsix.talecraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.sicsix.talecraft.ui.navigation.NavDrawer
import com.sicsix.talecraft.ui.navigation.NavGraph
import com.sicsix.talecraft.ui.navigation.TopAppBar
import com.sicsix.talecraft.ui.theme.TaleCraftTheme
import com.sicsix.talecraft.viewmodels.LibraryViewModel
import com.sicsix.talecraft.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaleCraftTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaleCraftApp()
                }
            }
        }
    }
}

@Composable
fun TaleCraftApp(userViewModel: UserViewModel = viewModel()) {
    // Navigation controller to manage app navigation
    val navController = rememberNavController()

    // Observe isLoggedIn to update UI
    val isLoggedIn by userViewModel.isLoggedIn.observeAsState(false)

    // Drawer state to manage navigation drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Coroutine scope to launch coroutines, used for navigation
    val scope = rememberCoroutineScope()

    // Scaffold composable to set up the app layout
    Scaffold(
        topBar = {
            // Show top navigation bar only if user is logged in
            if (isLoggedIn) {
                TopAppBar(scope, drawerState, navController)
            }
        }
    )
    { innerPadding ->
        // Navigation drawer to move between screens and log out
        if (isLoggedIn) {
            val libraryViewModel = hiltViewModel<LibraryViewModel>()
            NavDrawer(scope, drawerState, navController, userViewModel, libraryViewModel) {
                // NavGraph composable to define navigation graph
                NavGraph(navController, innerPadding, libraryViewModel)
            }
        }
        else {
            // NavGraph composable to define navigation graph
            NavGraph(navController, innerPadding)
        }
    }
}









