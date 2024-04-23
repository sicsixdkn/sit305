package com.sicsix.itube.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sicsix.itube.viewmodels.UserViewModel

@Composable
fun PlaylistScreen(navController: NavController, viewModel: UserViewModel) {
    // Collecting state from the ViewModel
    val entries by viewModel.playlistEntries.observeAsState(emptyList())
    val userId by viewModel.userId.observeAsState()

    // Load the playlist when the screen is first displayed
    viewModel.loadPlaylist()
    // Load the playlist when the user ID changes
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadPlaylist()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            // LazyColumn is used to display a list of items efficiently
            LazyColumn {
                // Static item for the title header
                item {
                    Text(
                        text = "My Playlist",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                // Dynamic items created for each entry using the items function
                items(entries) { entry ->
                    // Each entry is represented as a ListItem which becomes clickable
                    ListItem(
                        // Navigates to the video player screen when the ListItem is clicked
                        modifier = Modifier.clickable {
                            navController.navigate("play/${entry.videoId}")
                        },
                        headlineContent = { Text(entry.videoUrl) }
                    )
                    // A divider placed between each ListItem for better visual separation
                    HorizontalDivider()
                }
            }
        }
    }
}
