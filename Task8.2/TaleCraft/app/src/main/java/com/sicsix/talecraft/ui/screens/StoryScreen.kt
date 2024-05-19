package com.sicsix.talecraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sicsix.talecraft.viewmodels.StoryViewModel
import kotlinx.coroutines.delay

@Composable
fun StoryScreen(viewModel: StoryViewModel, storyId: Int = 0) {
    // Observe the story entries, loading state, and querying state
    val storyEntries by viewModel.storyEntries.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(true)
    val isQuerying by viewModel.isQuerying.observeAsState(false)
    // Observe the current story and world
    val story by viewModel.story.observeAsState()
    val world by viewModel.world.observeAsState()
    // Create a list state so we can automatically scroll to the bottom of the list
    val listState = rememberLazyListState()

    LaunchedEffect(storyId) {
        while (isLoading) {
            // Wait for the story to load
            delay(1000)
        }
        if (story != null && world != null && !isQuerying && storyEntries.isEmpty()) {
            // Generate the first story entry if it's a new story
            viewModel.generateStoryEntry()
        } else {
            // Scroll to the bottom of the list
            listState.animateScrollToItem(storyEntries.size - 1)
        }
    }

    LaunchedEffect(storyEntries, isQuerying) {
        // Scroll to the bottom of the list when new entries are added or when querying the LLM
        if (storyEntries.isNotEmpty()) {
            if (isQuerying) {
                // Scroll to the ellipsis when querying the LLM
                listState.animateScrollToItem(storyEntries.size)
            } else {
                // Scroll to the selected option when new entries are added
                listState.animateScrollToItem((storyEntries.size - 1))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(), state = listState
            ) {
                item {
                    Text(
                        text = story?.title ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(storyEntries) { entry ->

                    if (entry.isUserSelection) {
                        Spacer(modifier = Modifier.size(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = entry.content,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = "*     *     *",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = entry.content, textAlign = TextAlign.Justify, style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (isLoading || isQuerying) {
                    item {
                        var dots by remember { mutableStateOf(1) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                dots = (dots % 3) + 1
                                delay(500)
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "\u25CF".repeat(dots),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (storyEntries.isNotEmpty() && storyEntries.last().options.isNotEmpty()) {
                    val userOptions = storyEntries.last().options
                    item {
                        Spacer(modifier = Modifier.size(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.size(8.dp))
                        Column(Modifier.fillMaxWidth()) {
                            userOptions.forEach { option ->
                                Spacer(modifier = Modifier.size(4.dp))
                                OutlinedButton(onClick = {
                                    viewModel.generateStoryEntry(option)
                                }) {
                                    Text(
                                        text = option,
                                        fontStyle = FontStyle.Italic,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}