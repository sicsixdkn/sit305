package com.sicsix.talecraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sicsix.talecraft.viewmodels.SettingsViewModel
import com.sicsix.talecraft.viewmodels.StoryViewModel
import kotlinx.coroutines.delay

@Composable
fun StoryScreen(storyViewModel: StoryViewModel = hiltViewModel(), settingsViewModel: SettingsViewModel = hiltViewModel()) {
    // Observe the story entries, loading state, and querying state
    val storyEntries by storyViewModel.storyEntries.observeAsState(null)
    val isLoading by storyViewModel.isLoading.observeAsState(true)
    val isQuerying by storyViewModel.isQuerying.observeAsState(false)
    // Observe the current story
    val story by storyViewModel.story.observeAsState()
    // Create a list state so we can automatically scroll to the bottom of the list
    val listState = rememberLazyListState()

    // Observe the user's settings
    val useLargerReaderFont by settingsViewModel.useLargerReaderFont.observeAsState(false)
    val useLocalLLM by settingsViewModel.useLocalLLM.observeAsState(false)

    // Tracks whether the story is reverting, used to animate the scroll to the bottom of the list
    val isReverting = remember { mutableStateOf(false) }

    val textStyle = if (useLargerReaderFont == true) {
        MaterialTheme.typography.bodyLarge
    } else {
        MaterialTheme.typography.bodyMedium
    }

    LaunchedEffect(storyEntries, isQuerying) {
        if (storyEntries == null) {
            return@LaunchedEffect
        }
        if (storyEntries!!.isEmpty() && !isLoading && !isQuerying) {
            // Generate the first story entry if it's a new story
            storyViewModel.generateStoryEntry(useLocalLLM)
        } else {
            if (isQuerying) {
                // Scroll to the ellipsis when querying the LLM
                listState.animateScrollToItem(storyEntries!!.size)
            } else if (isReverting.value) {
                isReverting.value = false
                // Scroll to the user options
                listState.animateScrollToItem(storyEntries!!.size)
            } else {
                // Scroll to the latest story entry
                listState.animateScrollToItem(storyEntries!!.size - 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 16.dp)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp, 0.dp)
                    )
                }

                if (storyEntries == null) {
                    return@LazyColumn
                }

                items(storyEntries!!) { entry ->

                    if (entry.isUserSelection) {
                        Spacer(modifier = Modifier.size(8.dp))
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp, 0.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp)
                        ) {
                            Text(
                                text = entry.content,
                                fontStyle = FontStyle.Italic,
                                style = textStyle,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                // Hide the button but take the same amount of space in the layout
                                modifier = Modifier.alpha(if (isQuerying) 0f else 1f),
                                onClick = {
                                    // Revert the story to the given entry
                                    if (!isQuerying) {
                                        isReverting.value = true
                                        storyViewModel.revertStory(entry)
                                    }
                                }) {
                                Icon(
                                    imageVector = Icons.Filled.Undo,
                                    contentDescription = "Undo",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.Top)
                                        .padding(0.dp)
                                )
                            }
                        }

                    } else {
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = "*     *     *",
                            textAlign = TextAlign.Center,
                            style = textStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = entry.content,
                            textAlign = TextAlign.Justify,
                            style = textStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp, 0.dp)
                        )
                    }
                }

                if (isLoading || isQuerying) {
                    item {
                        var dots by remember { mutableIntStateOf(1) }
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
                            style = textStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (storyEntries!!.isNotEmpty() && storyEntries!!.last().options.isNotEmpty()) {
                    val userOptions = storyEntries!!.last().options
                    item {
                        Spacer(modifier = Modifier.size(8.dp))
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp, 0.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp, 0.dp)
                        ) {
                            userOptions.forEach { option ->
                                Spacer(modifier = Modifier.size(4.dp))
                                OutlinedButton(
                                    shape = MaterialTheme.shapes.small,
                                    onClick = {
                                        storyViewModel.generateStoryEntry(useLocalLLM, option)
                                    }) {
                                    Text(
                                        text = option,
                                        fontStyle = FontStyle.Italic,
                                        style = textStyle,
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