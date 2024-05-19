package com.sicsix.talecraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sicsix.talecraft.ui.screens.CreateStoryScreen
import com.sicsix.talecraft.ui.screens.CreateWorldScreen
import com.sicsix.talecraft.ui.screens.LibraryScreen
import com.sicsix.talecraft.ui.screens.LoginScreen
import com.sicsix.talecraft.ui.screens.SignUpScreen
import com.sicsix.talecraft.ui.screens.StoryScreen
import com.sicsix.talecraft.ui.screens.WorldsScreen
import com.sicsix.talecraft.ui.theme.TaleCraftTheme
import com.sicsix.talecraft.viewmodels.LibraryViewModel
import com.sicsix.talecraft.viewmodels.StoryViewModel
import com.sicsix.talecraft.viewmodels.UserViewModel
import com.sicsix.talecraft.viewmodels.WorldViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
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
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    Modifier.padding(top = 64.dp),
                ) {
                    NavigationDrawerItem(
                        label = { Text(text = "Library") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                navController.navigate(Screen.Library.route)
                                drawerState.close()
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Worlds") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                navController.navigate(Screen.Worlds.route)
                                drawerState.close()
                            }
                        }
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(text = "Sign out") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                userViewModel.logout()
                                navController.navigate(Screen.Login.route)
                                drawerState.close()
                            }
                        }
                    )
                }
            }
        ) {
            // NavHost manages navigation within the app
            NavHost(navController = navController, startDestination = Screen.Login.route, Modifier.padding(innerPadding)) {
                // Navigation graph definition
                composable(Screen.Login.route) {
                    val viewModel = hiltViewModel<UserViewModel>()
                    LoginScreen(navController, viewModel)
                }
                composable(Screen.SignUp.route) {
                    val viewModel = hiltViewModel<UserViewModel>()
                    SignUpScreen(navController, viewModel)
                }
                composable(Screen.Library.route) {
                    val viewModel = hiltViewModel<LibraryViewModel>()
                    LibraryScreen(navController, viewModel)
                }
                composable(Screen.Worlds.route) {
                    val viewModel = hiltViewModel<WorldViewModel>()
                    WorldsScreen(navController, viewModel)
                }
                composable(Screen.Story.route) {
                    val storyId = it.arguments?.getString("storyId")?.toInt() ?: return@composable
                    val viewModel = hiltViewModel<StoryViewModel>()
                    StoryScreen(viewModel, storyId)
                }
                composable(Screen.CreateWorld.route) {
                    val viewModel = hiltViewModel<WorldViewModel>()
                    CreateWorldScreen(navController, viewModel)
                }
                composable(Screen.CreateStory.route) {
                    val worldId = it.arguments?.getString("worldId")?.toInt() ?: return@composable
                    val viewModel = hiltViewModel<LibraryViewModel>()
                    CreateStoryScreen(navController, viewModel, worldId)
                }
            }
        }
    }
}

// Screen enum class to define the screens in the app
enum class Screen(val route: String, val title: String) {
    Login("login", "Login"),
    SignUp("signup", "Sign Up"),
    Library("library", "Library"),
    Story("story/{storyId}", "Story"),
    Worlds("worlds", "Worlds"),
    CreateStory("createStory/{worldId}", "Create Story"),
    CreateWorld("createWorld", "Create World"),
}

// Bottom navigation bar composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController
) {
    // Get the current navigation back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Get the current route
    val currentRoute = navBackStackEntry?.destination?.route

    // Map the route to the screen title
    val title = when (currentRoute) {
        Screen.Login.route -> Screen.Login.title
        Screen.SignUp.route -> Screen.SignUp.title
        Screen.Library.route -> Screen.Library.title
        Screen.Story.route -> Screen.Story.title
        Screen.Worlds.route -> Screen.Worlds.title
        else -> ""
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                )
            }
        },
    )
}









