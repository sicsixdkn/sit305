package com.sicsix.quizapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sicsix.quizapp.viewmodel.QuizViewModel

@Composable
@Preview(showBackground = true)
fun QuizScreen(
    navController: NavController = rememberNavController(),
    quizViewModel: QuizViewModel = viewModel()
) {
    // Collecting state from view model
    val username by quizViewModel.username.collectAsState()
    val questions by quizViewModel.questions.collectAsState()
    val currentIndex by quizViewModel.currentIndex.collectAsState()
    val selectedAnswerIndex by quizViewModel.selectedAnswerIndex.collectAsState()
    val answerSubmitted by quizViewModel.answerSubmitted.collectAsState()

    // Safe call to ensure a question is available, else return
    val currentQuestion = questions.getOrNull(currentIndex) ?: return

    // Calculate the progress of the quiz
    val progress = (currentIndex + 1f) / questions.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting message with the username
        Text(
            text = "Welcome $username!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Progress bar and question counter
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp, start = 32.dp, end = 32.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "${currentIndex + 1}/5",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp, 0.dp)
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            )
        }

        // Display current question
        Text(
            text = "Question ${currentIndex + 1}:",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            currentQuestion.questionText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        val buttonModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 24.dp)
            .fillMaxWidth()

        val buttonTextStyle = MaterialTheme.typography.labelLarge

        Spacer(modifier = Modifier.weight(1f))

        val answers =
            listOf(currentQuestion.answer1, currentQuestion.answer2, currentQuestion.answer3)

        // Display each possible answer as a button
        answers.forEachIndexed { index, answer ->
            val answerIndex = index + 1
            val isSelected = answerIndex == selectedAnswerIndex
            val isCorrect = answerIndex == currentQuestion.correctAnswerIndex
            val showError =
                answerSubmitted && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex

            Button(
                onClick = { quizViewModel.selectAnswer(answerIndex) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        // Modify the button colours based on selection/answer state
                        answerSubmitted && isCorrect -> Color(0xFF2E7D32)
                        showError -> MaterialTheme.colorScheme.error
                        isSelected -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                ),
                modifier = buttonModifier
            ) {
                Text(text = answer, style = buttonTextStyle)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Operates as either a SUBMIT or NEXT button depending on the current state
        Button(
            onClick = {
                if (!answerSubmitted)
                    quizViewModel.submitAnswer()
                else if (!quizViewModel.moveToNextQuestion()) // false returned here means no questions left
                    navController.navigate("resultScreen")
            },
            modifier = buttonModifier
        ) {
            Text(text = if (!answerSubmitted) "SUBMIT" else "NEXT", style = buttonTextStyle)
        }
    }
}