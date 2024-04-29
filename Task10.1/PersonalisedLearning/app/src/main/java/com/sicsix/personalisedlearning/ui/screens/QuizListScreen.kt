package com.sicsix.personalisedlearning.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sicsix.personalisedlearning.viewmodels.QuizListViewModel

@Composable
fun QuizListScreen(navController: NavController, viewModel: QuizListViewModel = viewModel()) {
    // Collecting state from the ViewModel
    val quizzes = viewModel.quizzes.observeAsState(emptyList()).value
    // Calculate the number of incomplete quizzes
    val incompleteQuizzesCount = quizzes.count { !it.complete }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier.padding(0.dp, 12.dp)
                ) {
                    Text(
                        text = "Hello,",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = "${viewModel.getUserDetails()?.username}",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Account",
                    modifier = Modifier
                        .size(112.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            if (incompleteQuizzesCount < 3) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Generating quizzes...",
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            LazyColumn {
                items(quizzes) { quiz ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = if (quiz.complete) CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest) else CardDefaults.elevatedCardColors()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = quiz.topic,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = if (quiz.complete) "Completed - Scored (${quiz.score}/3)" else "Incomplete",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            if (!quiz.complete) {
                                Button(
                                    modifier = Modifier.align(Alignment.BottomEnd),
                                    onClick = {
                                        navController.navigate("quiz/${quiz.quiz_id}")
                                    }
                                ) {
                                    Text("Go!")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun QuizListScreenPreview() {
    QuizListScreen(navController = rememberNavController())
}