package com.sicsix.quizapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sicsix.quizapp.viewmodel.QuizViewModel

@Composable
@Preview(showBackground = true)
fun ResultScreen(
    navController: NavController = rememberNavController(),
    quizViewModel: QuizViewModel = viewModel()
) {
    // Getting the current context to use for finishing the activity
    val context = LocalContext.current
    // Collecting state from view model
    val username by quizViewModel.username.collectAsState()
    val score by quizViewModel.score.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(16.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Spacer used to push the content towards the center
        Spacer(modifier = Modifier.weight(1f))

        // Congratulates the user by name
        Text(
            text = "Congratulations $username!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "YOUR SCORE:",
            style = MaterialTheme.typography.headlineSmall
        )

        // Displays the user's score
        Text(
            text = "${score}/5",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val buttonModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 24.dp)
            .fillMaxWidth()

        // Button to retake the quiz. This resets the quiz state and navigates back to the main screen
        Button(
            onClick = {
                navController.navigate("mainScreen")
                quizViewModel.reset()
            },
            modifier = buttonModifier
        ) {
            Text("TAKE NEW QUIZ", style = MaterialTheme.typography.labelLarge)
        }

        // Button to exit the application
        // This uses the current context to call finish() on the Activity with a guard to allow
        // the composable preview to still function
        Button(
            onClick = {
                if (context is Activity)
                    context.finish()
            },
            modifier = buttonModifier
        ) {
            Text("FINISH", style = MaterialTheme.typography.labelLarge)
        }

        // Spacer used to push the content towards the center
        Spacer(modifier = Modifier.weight(1f))
    }
}