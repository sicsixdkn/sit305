package com.sicsix.itube.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sicsix.itube.utility.Utility.Companion.validateEntry
import com.sicsix.itube.viewmodels.UserViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {
    // Local state for the username and password input fields
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Local state for the error messages
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    // Observing the LiveData of login result from the ViewModel and collecting it as state so compose can react to data changes.
    val loginResult by viewModel.loginSuccess.observeAsState()
    var showDialog by remember { mutableStateOf(false) }

    // Launch effect to navigate to the HomeScreen when login is successful
    LaunchedEffect(loginResult) {
        when (loginResult) {
            true -> navController.navigate("home")
            false -> showDialog = true
            null -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "iTUBE",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge
            )
            OutlinedTextField(
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
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                value = password,
                onValueChange = {
                    password = it
                    validateEntry(it)
                },
                label = { Text(text = "Password") },
                singleLine = true,
                isError = passwordError != null,
                supportingText = {
                    if (passwordError == null) {
                        Text("")
                    } else {
                        Text(passwordError!!)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, "Toggle Visibility")
                    }
                }
            )
            Button(
                onClick = {
                    // Validate the username and password before attempting to log in
                    usernameError = validateEntry(username)
                    passwordError = validateEntry(password)
                    if (usernameError != null || passwordError != null) {
                        return@Button
                    }

                    viewModel.login(username, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Log In")
            }
            OutlinedButton(
                onClick = {
                    navController.navigate("signup")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }
        }
    }

    // Display an alert dialog if login fails
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                viewModel.resetLoginSuccess()
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        viewModel.resetLoginSuccess()
                    }
                ) {
                    Text("OK")
                }
            },
            title = { Text("Login Failed") },
            text = { Text("Please check your username and password and try again.") }
        )
    }
}