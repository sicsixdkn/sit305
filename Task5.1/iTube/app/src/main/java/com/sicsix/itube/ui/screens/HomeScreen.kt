package com.sicsix.itube.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sicsix.itube.utility.Utility
import com.sicsix.itube.viewmodels.UserViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: UserViewModel) {
    // Local state for the YouTube URL input field
    var url by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                value = url,
                onValueChange = {
                    url = it
                },
                label = { Text("YouTube URL") },
                singleLine = true
            )

            Button(
                onClick = {
                    if (url.isEmpty())
                        return@Button
                    // Extract the video ID from the URL
                    val videoId = Utility.extractVideoId(url)
                    if (videoId.isNullOrEmpty())
                        return@Button
                    // Navigate to the PlayScreen with the video ID
                    navController.navigate("play/${videoId}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Play")
            }

            OutlinedButton(
                onClick = {
                    if (url.isEmpty())
                        return@OutlinedButton
                    // Extract the video ID from the URL
                    val videoId = Utility.extractVideoId(url)
                    if (videoId.isNullOrEmpty())
                        return@OutlinedButton
                    // Add the URL to the playlist
                    viewModel.insertPlaylistEntry(url)
                    url = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Add to Playlist")
            }

            OutlinedButton(
                onClick = {
                    navController.navigate("playlist")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "My Playlist")
            }
        }
    }
}