package com.sicsix.taskmanager.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Enum class to represent each navigation item in the bottom navigation bar.
 *
 * @property route The navigation route associated with the navigation item.
 * @property icon The icon to display for the navigation item.
 * @property label The label text for the navigation item.
 */
enum class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    TASK_LIST("taskList", Icons.AutoMirrored.Filled.List, "Tasks"),
    ADD_TASK("addTask", Icons.Filled.Add, "Add Task")
}

/**
 * Main screen of the application, hosting the navigation system and the scaffold structure.
 *
 * This composable sets up the navigation graph and the bottom navigation bar, and defines the overall layout
 * of the application's main screen.
 */
@Composable
@Preview(showBackground = true)
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(BottomNavItem.TASK_LIST, BottomNavItem.ADD_TASK)

    Scaffold(
        bottomBar = { BottomNavigationBar(items, navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.TASK_LIST.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.TASK_LIST.route) { TaskListScreen(navController) }
            composable(BottomNavItem.ADD_TASK.route) { TaskScreen(navController, taskId = null) }
            composable("task/{taskId}") { backStackEntry ->
                TaskScreen(navController, backStackEntry.arguments?.getString("taskId"))
            }
        }
    }
}

/**
 * Defines and constructs the bottom navigation bar for the application.
 *
 * @param items The list of navigation items to display in the bottom bar.
 * @param navController The NavController that manages navigation within the composable.
 */
@Composable
fun BottomNavigationBar(items: List<BottomNavItem>, navController: NavController) {
    NavigationBar {
        val currentRoute = navController.currentDestination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (navController.currentBackStackEntry?.destination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}