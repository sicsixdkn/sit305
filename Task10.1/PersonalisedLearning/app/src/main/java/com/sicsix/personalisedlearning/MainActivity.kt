package com.sicsix.personalisedlearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sicsix.personalisedlearning.ui.screens.HistoryScreen
import com.sicsix.personalisedlearning.ui.screens.InterestsScreen
import com.sicsix.personalisedlearning.ui.screens.LoginScreen
import com.sicsix.personalisedlearning.ui.screens.ProfileScreen
import com.sicsix.personalisedlearning.ui.screens.QuizListScreen
import com.sicsix.personalisedlearning.ui.screens.QuizScreen
import com.sicsix.personalisedlearning.ui.screens.ResultsScreen
import com.sicsix.personalisedlearning.ui.screens.SignUpScreen
import com.sicsix.personalisedlearning.ui.screens.UpgradeScreen
import com.sicsix.personalisedlearning.ui.theme.PersonalisedLearningTheme
import com.sicsix.personalisedlearning.viewmodels.HistoryViewModel
import com.sicsix.personalisedlearning.viewmodels.ProfileViewModel
import com.sicsix.personalisedlearning.viewmodels.QuizListViewModel
import com.sicsix.personalisedlearning.viewmodels.QuizViewModel
import com.sicsix.personalisedlearning.viewmodels.UpgradeViewModel
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
fun PersonalisedLearningApp(userViewModel: UserViewModel = viewModel()) {
    // Navigation controller to manage app navigation
    val navController = rememberNavController()

    // List of items in the bottom navigation bar
    val items = listOf(Screen.QuizList, Screen.Upgrade, Screen.Profile)

    // Observe isLoggedIn to update UI
    val isLoggedIn by userViewModel.isLoggedIn.observeAsState(false)


    // Scaffold composable to set up the app layout
    Scaffold(
        bottomBar = {
            // Show bottom navigation bar only if user is logged in
            if (isLoggedIn) {
                BottomNavigationBar(items, navController)
            }
        }
    )
    { innerPadding ->
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
            composable(Screen.Interests.route) {
                val viewModel = hiltViewModel<UserViewModel>()
                InterestsScreen(navController, viewModel)
            }
            composable(Screen.QuizList.route) {
                val viewModel = hiltViewModel<QuizListViewModel>()
                QuizListScreen(navController, viewModel)
            }
            composable(Screen.Quiz.route) {
                val viewModel = hiltViewModel<QuizViewModel>()
                val quizId = it.arguments?.getString("quizId") ?: return@composable
                QuizScreen(navController, viewModel, quizId)
            }
            composable(Screen.Results.route) {
                val viewModel = hiltViewModel<QuizViewModel>()
                val quizId = it.arguments?.getString("quizId") ?: return@composable
                ResultsScreen(navController, viewModel, quizId)
            }
            composable(Screen.Profile.route) {
                val viewModel = hiltViewModel<ProfileViewModel>()
                ProfileScreen(navController, viewModel)
            }
            composable(Screen.Upgrade.route) {
                val viewModel = hiltViewModel<UpgradeViewModel>()
                UpgradeScreen(viewModel)
            }
            composable(Screen.History.route) {
                val viewModel = hiltViewModel<HistoryViewModel>()
                val historyType = it.arguments?.getString("historyType") ?: return@composable
                HistoryScreen(viewModel, historyType)
            }
        }
    }
}

// Screen enum class to define the screens in the app
enum class Screen(val route: String, val icon: ImageVector? = null) {
    Login("login"),
    SignUp("signup"),
    Interests("interests"),
    QuizList("quizlist", Icons.Default.Quiz),
    Quiz("quiz/{quizId}"),
    Results("results/{quizId}"),
    Profile("profile", Icons.Default.AccountCircle),
    Upgrade("upgrade", Icons.Default.Upgrade),
    History("history/{historyType}"),
}

// Bottom navigation bar composable
@Composable
fun BottomNavigationBar(items: List<Screen>, navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // Create a NavigationBarItem for each passed in screen
        items.forEach { screen ->
            NavigationBarItem(
                icon = { screen.icon?.let { Icon(it, contentDescription = screen.route) } },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route)
                }
            )
        }
    }
}









