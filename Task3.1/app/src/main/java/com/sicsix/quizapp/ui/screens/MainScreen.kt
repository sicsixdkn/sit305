package com.sicsix.quizapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sicsix.quizapp.viewmodel.QuizViewModel

@Composable
@Preview(showBackground = true)
fun MainScreen(
    navController: NavController = rememberNavController(),
    quizViewModel: QuizViewModel = viewModel()
) {
    // Collecting state from view model
    val username by quizViewModel.username.collectAsState()

    // Local state for username entry, uses existing username in view model if it exists
    val (textFieldValue, setTextFieldValue) = rememberSaveable {
        mutableStateOf(username ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(16.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "SIT305 Quiz App",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spacer used to push the content towards the center
        Spacer(modifier = Modifier.weight(1f))

        // Text input for user name
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = setTextFieldValue,
            label = { Text("Enter your name", style = MaterialTheme.typography.bodyLarge) },
            singleLine = true,
            modifier = Modifier
                .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
        )

        val buttonModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 24.dp)
            .fillMaxWidth()

        // Button to start the quiz, disabled if the username field is empty
        Button(
            onClick = {
                quizViewModel.setUsername(textFieldValue)
                navController.navigate("quizScreen")
            },
            enabled = textFieldValue.trim().isNotEmpty(),
            modifier = buttonModifier
        ) {
            Text("START", style = MaterialTheme.typography.labelLarge)
        }

        // Spacer used to push the content towards the center
        Spacer(modifier = Modifier.weight(1f))
    }
}