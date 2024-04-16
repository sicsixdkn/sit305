package com.sicsix.taskmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.taskmanager.viewmodel.TaskViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A composable function that displays a screen with a list of tasks.
 *
 * Each task is presented in a list item that can be clicked to navigate to a detailed view of the task.
 * The tasks are fetched from a ViewModel which observes LiveData from the repository.
 *
 * @param navController A NavController to handle navigation based on user actions.
 * @param viewModel The ViewModel that holds the logic to access task data.
 */
@Composable
fun TaskListScreen(navController: NavController, viewModel: TaskViewModel = viewModel()) {
    // Observing the LiveData of tasks from the ViewModel and collecting it as state so compose can react to data changes.
    val tasks = viewModel.allTasks.observeAsState(emptyList()).value

    // Using LazyColumn to efficiently display a potentially large list of tasks.
    LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
        // Static item for the title header.
        item {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(8.dp)
            )
        }
        // Dynamic items created for each task using the items function.
        items(tasks) { task ->
            // Each task is represented as a ListItem which becomes clickable.
            ListItem(
                // Navigates to the task detail screen when the ListItem is clicked.
                modifier = Modifier.clickable {
                    navController.navigate("task/${task.id}")
                },
                // Main text showing the task's title.
                headlineContent = { Text(task.title) },
                supportingContent = {
                    // Formatter to convert the epoch time to a readable date format.
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        .withLocale(Locale.getDefault())
                        .withZone(ZoneId.systemDefault())
                    // Displaying the task's due date formatted as day-month-year.
                    Text(formatter.format(Instant.ofEpochMilli(task.dueDate)))
                }
            )
            // A divider placed between each ListItem for better visual separation.
            HorizontalDivider()
        }
    }
}