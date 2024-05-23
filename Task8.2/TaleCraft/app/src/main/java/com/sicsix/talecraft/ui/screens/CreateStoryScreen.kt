package com.sicsix.talecraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sicsix.talecraft.models.World
import com.sicsix.talecraft.utility.Utility
import com.sicsix.talecraft.viewmodels.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryScreen(navController: NavController, worldId: Int, viewModel: LibraryViewModel = hiltViewModel()) {
    // Local state for the title text field
    var title by rememberSaveable { mutableStateOf("") }
    // Local state for the error message
    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    // Local state for the world dropdown
    var worldExpanded by remember { mutableStateOf(false) }
    var selectedWorld by remember { mutableStateOf<World?>(null) }

    // Observe the worlds from the view model
    val worlds by viewModel.worlds.observeAsState()

    LaunchedEffect(worlds) {
        // Set the selected world when the worlds are loaded
        if (!worlds.isNullOrEmpty()) {
            selectedWorld = if (worldId != -1) {
                // If the user is creating a story from a world, set the selected world to that world
                worlds!!.find { it.id == worldId }
            } else {
                // Otherwise, set the selected world to the first world
                worlds!!.first()
            }

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Story",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(128.dp)
            )

            Column(
                Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Let's create a new story!",
                    style = MaterialTheme.typography.headlineMedium
                )
            }


            Spacer(modifier = Modifier.weight(0.33f))

            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = {
                        title = it
                        Utility.validateEntry(it)
                    },
                    label = { Text(text = "Title") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) {
                            Text(titleError!!)
                        }
                    },
                )

                ExposedDropdownMenuBox(
                    expanded = worldExpanded,
                    onExpandedChange = { worldExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedWorld?.title ?: "",
                        onValueChange = {},
                        label = { Text("World") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = worldExpanded) },
                    )
                    ExposedDropdownMenu(expanded = worldExpanded, onDismissRequest = { worldExpanded = false }) {
                        worlds?.forEach { world ->
                            DropdownMenuItem(text = { Text(text = world.title) }, onClick = {
                                selectedWorld = world
                                worldExpanded = false
                            })

                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        titleError = Utility.validateEntry(title)

                        if (titleError != null || selectedWorld == null) {
                            return@Button
                        }
                        // Insert the story into the database
                        viewModel.insertStory(
                            title = title,
                            worldTitle = selectedWorld!!.title,
                            worldId = selectedWorld!!.id
                        )
                        // Navigate back to the library screen
                        navController.navigate("library")
                    }, modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth()
                ) {
                    Text(text = "Create story")
                }
            }
        }
    }
}