package com.sicsix.personalisedlearning.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.personalisedlearning.viewmodels.QuizViewModel

@Composable
fun ResultsScreen(navController: NavController, viewModel: QuizViewModel = viewModel(), quizId: String) {
    // Observe the quiz from the ViewModel and collect it as state so compose can react to data changes.
    val quiz by viewModel.quiz.observeAsState()

    // Remember whether the quiz has been loaded
    val quizLoaded = remember { mutableStateOf(false) }

    if (!quizLoaded.value) {
        viewModel.loadQuiz(quizId)
        quizLoaded.value = true
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
            Column(
                Modifier
                    .padding(0.dp, 24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Your results",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Left
                )
            }
            if (quiz == null) {
                return@Column
            }

            LazyColumn {
                itemsIndexed(quiz!!.questions) { questionIndex, question ->
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
                            Column {
                                Text(
                                    text = "${questionIndex + 1}: ${question.question}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "You answered: ${quiz!!.selected_answers[questionIndex]}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Correct answer is: ${question.correct_answer}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = question.options[question.correct_answer[0].code - 'A'.code],
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Your score: ${quiz!!.score}/${quiz!!.questions.size}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Button(
                        onClick = {
                            navController.navigate("quizlist")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 12.dp)
                    ) {
                        Text(text = "Return to quiz list")
                    }
                }
            }
        }
    }
}