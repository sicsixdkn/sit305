package com.sicsix.personalisedlearning.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.personalisedlearning.viewmodels.QuizViewModel
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(navController: NavController, viewModel: QuizViewModel = viewModel(), quizId: String) {
    // Observe the quiz from the ViewModel and collect it as state so compose can react to data changes.
    val quiz by viewModel.quiz.observeAsState()

    // Initialize selectedOptions with an empty list
    val (selectedOptions, setSelectedOptions) = remember { mutableStateOf<List<Int?>>(emptyList()) }

    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope

    // Update selectedOptions whenever quiz changes
    LaunchedEffect(quiz) {
        setSelectedOptions(List(quiz?.questions?.size ?: 0) { null })
    }

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
            if (quiz == null || selectedOptions.isEmpty()) {
                return@Column
            }

            Column(
                Modifier
                    .padding(0.dp, 24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = quiz!!.topic,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Left
                )
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
                            Column(Modifier.selectableGroup()) {
                                Text(
                                    text = "${questionIndex + 1}: ${question.question}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                question.options.forEachIndexed { answerIndex, option ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = (answerIndex == selectedOptions[questionIndex]),
                                                onClick = {
                                                    setSelectedOptions(
                                                        selectedOptions
                                                            .toMutableList()
                                                            .apply {
                                                                this[questionIndex] = answerIndex
                                                            })
                                                },
                                                role = Role.RadioButton
                                            )
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        RadioButton(
                                            selected = (answerIndex == selectedOptions[questionIndex]),
                                            onClick = null,
                                            modifier = Modifier.align(Alignment.Top)
                                        )
                                        Text(
                                            text = "${'A' + answerIndex}",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .align(Alignment.Top)
                                        )
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                                .padding(start = 8.dp, top = 3.dp)
                                                .align(Alignment.Top)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            lifecycleScope.launch {
                                val selectedOptionsLetters = selectedOptions.map { option ->
                                    when (option) {
                                        0 -> "A"
                                        1 -> "B"
                                        2 -> "C"
                                        3 -> "D"
                                        else -> "Unknown"
                                    }
                                }
                                val uploadSuccessful = viewModel.uploadQuizSelections(quizId, selectedOptionsLetters)
                                if (uploadSuccessful) {
                                    navController.navigate("results/${quizId}")
                                }
                            }
                            navController.navigate("results/${quizId}")
                        },
                        enabled = selectedOptions.all { it != null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 12.dp)
                    ) {
                        Text(text = "Done!")
                    }
                }
            }
        }
    }
}