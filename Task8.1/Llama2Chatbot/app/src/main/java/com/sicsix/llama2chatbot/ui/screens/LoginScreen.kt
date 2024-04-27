package com.sicsix.llama2chatbot.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.llama2chatbot.utility.Utility.Companion.validateEntry
import com.sicsix.llama2chatbot.viewmodels.AppViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AppViewModel = viewModel()) {
    // Local state for the username input field
    var username by rememberSaveable { mutableStateOf("") }

    // Local state for the error message
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }

    // Observing the LiveData of login result from the ViewModel and collecting it as state so compose can react to data changes.
    val loginSuccess by viewModel.loginSuccess.observeAsState()

    // If login is successful, navigate to the chat screen
    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            navController.navigate("chat")
            // Reset the login success state to false to prevent navigating again when returning to the login screen
            viewModel.setLoginSuccess(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 128.dp)
            ) {
                Text(
                    text = "Welcome,",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "Lets Chat!",
                    style = MaterialTheme.typography.displayLarge
                )
            }


            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = {
                    username = it
                    validateEntry(it)
                },
                label = { Text("Username") },
                singleLine = true,
                isError = usernameError != null,
                supportingText = {
                    if (usernameError == null) {
                        Text("")
                    } else {
                        Text(usernameError!!)
                    }
                }
            )

            Button(
                onClick = {
                    // Validate the username before attempting to log in
                    usernameError = validateEntry(username)
                    if (usernameError != null) {
                        return@Button
                    }

                    // Call the login function in the ViewModel
                    viewModel.login(username)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(text = "Go")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}