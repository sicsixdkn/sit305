package com.sicsix.talecraft.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sicsix.talecraft.viewmodels.LibraryViewModel
import com.sicsix.talecraft.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavDrawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController,
    userViewModel: UserViewModel,
    libraryViewModel: LibraryViewModel,
    content: @Composable () -> Unit
) {
    if (libraryViewModel.activeStory.value == null) {
        libraryViewModel.getActiveStory()
    }

    val activeStory by libraryViewModel.activeStory.observeAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                Modifier.padding(top = 64.dp),
            ) {
                if (activeStory != null) {
                    NavigationDrawerItem(
                        label = { Text(text = activeStory!!.title) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                navController.navigate("story/${activeStory!!.id}")
                                drawerState.close()
                            }
                        }
                    )
                    HorizontalDivider()
                }

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
                    label = { Text(text = "Settings") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            navController.navigate(Screen.Settings.route)
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
        content()
    }
}