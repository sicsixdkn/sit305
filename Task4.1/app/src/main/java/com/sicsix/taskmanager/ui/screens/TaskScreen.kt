package com.sicsix.taskmanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.taskmanager.model.Task
import com.sicsix.taskmanager.viewmodel.TaskViewModel

/**
 * Composable function that creates a screen for adding or editing tasks.
 *
 * This screen provides input fields for the task title, description, and due date. It uses a ViewModel
 * to manage saving and loading tasks, and navigates back to the task list upon completion.
 *
 * @param navController The NavController to handle navigation.
 * @param taskId The ID of the task if it's being edited, or null if a new task is being added.
 * @param viewModel The ViewModel that manages task operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navController: NavController,
    taskId: String?,
    viewModel: TaskViewModel = viewModel()
) {
    // Observes changes to the task LiveData from the ViewModel and represents it as state within the composable.
    // This allows the composable to automatically update when the task data changes.
    val task by viewModel.task.observeAsState()

    // State variables to store title, description, and due date.
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    val dueDate = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )

    // State variables to store potential validation errors for title and description.
    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var descriptionError by rememberSaveable { mutableStateOf<String?>(null) }

    // Effect to load task details when taskId changes or on initial load.
    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTaskById(taskId.toInt())
        }
    }

    // Reactively update the title and description when the task changes, such as on screen load
    LaunchedEffect(task) {
        title = task?.title ?: ""
        description = task?.description ?: ""
        task?.dueDate?.let { dueDate.selectedDateMillis = it }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Title text field, with validation and error messages
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                value = title,
                onValueChange = {
                    title = it
                    titleError = validateTitle(it)
                },
                label = { Text("Title") },
                isError = titleError != null,
                supportingText = {
                    if (titleError == null) {
                        Text("")
                    } else {
                        Text(titleError!!)
                    }
                },
                singleLine = true
            )

            // Description text field, with validation and error messages
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = validateDescription(it)
                },
                label = { Text("Description") },
                isError = descriptionError != null,
                supportingText = {
                    if (descriptionError == null) {
                        Text("")
                    } else {
                        Text(descriptionError!!)
                    }
                },
                singleLine = true
            )

            // Date selection composable
            DatePicker(
                state = dueDate,
                modifier = Modifier.padding(4.dp)
            )

            // Save and Delete buttons with validation check before performing actions.
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    onClick = {
                        titleError = validateTitle(title)
                        descriptionError = validateDescription(description)

                        if (titleError != null || descriptionError != null) {
                            return@Button
                        }

                        if (taskId == null) {
                            viewModel.insert(
                                Task(
                                    title = title,
                                    description = description,
                                    dueDate = dueDate.selectedDateMillis!!
                                )
                            )
                        } else {
                            viewModel.update(
                                Task(
                                    id = taskId.toInt(),
                                    title = title,
                                    description = description,
                                    dueDate = dueDate.selectedDateMillis!!
                                )
                            )
                        }
                        navController.navigate("taskList")
                    }) {
                    Text("Save")
                }

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = {
                        if (taskId != null) {
                            task?.let { viewModel.delete(it) }
                        }
                        navController.navigate("taskList")
                    }) {
                    Text("Delete")
                }
            }

        }
    }
}

/**
 * Validates the task title and returns an error message if invalid.
 */
fun validateTitle(name: String): String? {
    return if (name.isBlank()) "Name cannot be empty" else null
}

/**
 * Validates the task description and returns an error message if invalid.
 */
fun validateDescription(description: String): String? {
    return if (description.isBlank()) "Description cannot be empty" else null
}