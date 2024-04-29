package com.sicsix.personalisedlearning.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sicsix.personalisedlearning.viewmodels.HistoryViewModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel(), historyType: String) {
    // Observe the history from the ViewModel and collect it as state so compose can react to data changes.
    val answeredQuestions by viewModel.answeredQuestions.observeAsState()

    // Remember whether the history has been loaded
    val historyLoaded = remember { mutableStateOf(false) }

    // Load the history if it hasn't been loaded yet
    if (!historyLoaded.value) {
        viewModel.loadAnsweredQuestions(historyType)
        historyLoaded.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (answeredQuestions == null) {
                // Show a loading indicator while the history is being loaded
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Checking your answers...",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
                CircularProgressIndicator()
                Spacer(Modifier.weight(1f))
                return@Box
            }

            Column(
                Modifier
                    .padding(0.dp, 24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${historyType.capitalize()} Answers",
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Left
                )
            }

            LazyColumn {
                itemsIndexed(answeredQuestions!!) { index, answeredQuestion ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Column(Modifier.selectableGroup()) {
                                Text(
                                    text = answeredQuestion.topic,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "${index + 1}: ${answeredQuestion.question}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                answeredQuestion.options.forEachIndexed { answerIndex, option ->
                                    // Convert the answer index to a character
                                    val answerChar = "${'A' + answerIndex}"

                                    // Determine the color of the text based on the correctness of the answer
                                    val color: Color = if (answeredQuestion.correct_answer == answerChar) {
                                        Color(0xFF008000)
                                    } else if (answeredQuestion.selected_answer == answerChar) {
                                        Color(0xFFD00000)
                                    } else {
                                        Color.Black
                                    }

                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${'A' + answerIndex}",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .align(Alignment.Top),
                                            color = color
                                        )
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                                .padding(start = 8.dp, top = 0.dp)
                                                .align(Alignment.Top),
                                            color = color
                                        )
                                    }
                                }

                                // Show the reasoning if it is available (it will be generated by the bot for incorrect answers)
                                if (!answeredQuestion.reasoning.isNullOrEmpty()) {
                                    Text(
                                        text = "Reasoning",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(top = 8.dp),
                                    )
                                    Text(
                                        text = answeredQuestion.reasoning,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(horizontal = 4.dp),
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