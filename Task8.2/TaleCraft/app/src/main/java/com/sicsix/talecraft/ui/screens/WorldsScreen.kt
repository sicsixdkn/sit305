package com.sicsix.talecraft.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.talecraft.models.World
import com.sicsix.talecraft.viewmodels.WorldViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldsScreen(navController: NavController, viewModel: WorldViewModel = viewModel()) {
    // Observe the worlds from the view model
    val worlds by viewModel.worlds.observeAsState()
    // Remembers the world and dismiss box that was swiped when deleting
    val swipedWorld = remember { mutableStateOf<World?>(null) }
    val swipedDismissBox = remember { mutableStateOf<SwipeToDismissBoxState?>(null) }
    // Remembers the dialog state
    val showDialog = remember { mutableStateOf(false) }
    // Coroutine scope for the dialog
    val coroutineScope = rememberCoroutineScope()

    ConfirmDialog(
        showDialog = showDialog.value,
        title = "Confirm Delete",
        content = "Are you sure you want to delete this world? Any stories that use this world will be deleted too!",
        onConfirm = {
            // Delete the world when the dialog is confirmed
            viewModel.deleteWorld(swipedWorld.value!!)
            showDialog.value = false
        },
        onCancel = {
            // Reset the dismiss box when the dialog is cancelled
            showDialog.value = false
            coroutineScope.launch {
                swipedDismissBox.value!!.reset()
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 8.dp)
    ) {
        LazyColumn {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    onClick = {
                        navController.navigate("createWorld")
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Create a new world",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                }
            }

            items(worlds ?: listOf(), key = { world -> world.id }) { world ->
                val dismissBoxState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { size -> size * 0.9f }
                )

                SwipeToDismissBox(
                    state = dismissBoxState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = false,
                    backgroundContent = { },
                    content = {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            onClick = {
                                navController.navigate("createStory/${world.id}")
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 12.dp)
                            )
                            {
                                Text(
                                    text = world.title,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "${world.genre} - ${world.subGenre}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = world.premise,
                                    style = MaterialTheme.typography.bodyMedium,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                )

                LaunchedEffect(dismissBoxState.currentValue) {
                    // Show the dialog when the dismiss box is swiped
                    if (dismissBoxState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                        swipedWorld.value = world
                        swipedDismissBox.value = dismissBoxState
                        showDialog.value = true
                    }
                }
            }
        }
    }
}