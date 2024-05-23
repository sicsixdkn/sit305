package com.sicsix.talecraft.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Top app bar composable
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
        Screen.CreateStory.route -> Screen.CreateStory.title
        Screen.CreateWorld.route -> Screen.CreateWorld.title
        Screen.Settings.route -> Screen.Settings.title
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