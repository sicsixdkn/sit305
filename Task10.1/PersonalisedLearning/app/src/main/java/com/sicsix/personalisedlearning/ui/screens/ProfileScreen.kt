package com.sicsix.personalisedlearning.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.personalisedlearning.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val user = viewModel.getUserDetails()
    if (user == null) {
        // Navigate to login screen if user is not logged in
        navController.navigate("login")
        return
    }

    // Collecting state from the ViewModel
    val totalQuestions by viewModel.totalQuestions.observeAsState()
    val correctAnswers by viewModel.correctAnswers.observeAsState()
    val quizzesReady by viewModel.quizzesReady.observeAsState()

    // Context for creating the share intent
    val context = LocalContext.current

    // Load the stats
    viewModel.getStats()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(Modifier.weight(1f))

            ElevatedCard {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = user.username, style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = user.email, style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Account",
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            if (quizzesReady != null && quizzesReady!! > 0) {
                Card(Modifier.clickable(onClick = {
                    // Navigate to quiz list screen
                    navController.navigate("quizlist")
                })) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alert",
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "You have ${quizzesReady!!} quizzes ready to go!",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedCard(
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable {
                            navController.navigate("history/all")
                        },
                ) {
                    Column {
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(bottom = 2.dp)
                        ) {
                            Text(
                                text = "Total Questions",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 4.dp)
                            )
                        }

                        Box(
                            Modifier
                                .weight(1f)
                                .padding(top = 2.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QuestionAnswer,
                                    contentDescription = "Questions",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.CenterVertically)
                                )
                                Text(
                                    text = totalQuestions?.toString() ?: "0",
                                    style = MaterialTheme.typography.displaySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
                ElevatedCard(
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable {
                            navController.navigate("history/correct")
                        },
                ) {
                    Column {
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(bottom = 2.dp)
                        ) {
                            Text(
                                text = "Correctly Answered",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 4.dp)
                            )
                        }

                        Box(
                            Modifier
                                .weight(1f)
                                .padding(top = 2.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TaskAlt,
                                    contentDescription = "Correct",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.CenterVertically)
                                )
                                Text(
                                    text = correctAnswers?.toString() ?: "0",
                                    style = MaterialTheme.typography.displaySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            ElevatedCard(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .clickable {
                        navController.navigate("history/incorrect")
                    },
            ) {
                Row() {
                    Box(
                        Modifier.weight(0.5f)
                    ) {
                        Column {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = "Incorrect Answers",
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(horizontal = 4.dp)
                                )
                            }

                            Box(
                                Modifier
                                    .weight(1f)
                                    .padding(top = 2.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Incorrect",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .align(Alignment.CenterVertically)
                                    )
                                    Text(
                                        text = (totalQuestions?.minus(correctAnswers ?: 0)).toString(),
                                        style = MaterialTheme.typography.displaySmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        Modifier
                            .weight(0.5f)
                            .fillMaxSize()
                    ) {
                        OutlinedButton(
                            onClick = {
                                navController.navigate("history/incorrect")
                            },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text("Get feedback!")
                        }
                    }
                }
            }

            Button(
                onClick = {
                    // Create the share intent, sharing the user's profile as a link
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey, check out my profile in the Personalised Learning app! http://10.0.2.2:5000/sharedprofile/${user.id}"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "Share", style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}