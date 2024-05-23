package com.sicsix.talecraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.talecraft.utility.Utility.Companion.validateEntry
import com.sicsix.talecraft.viewmodels.UserViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel = hiltViewModel()) {
    // Local state for the username and password input fields
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    // Local state for the error messages
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    // Observing the LiveData of login result from the ViewModel and collecting it as state so compose can react to data changes.
    val isLoggedIn by viewModel.isLoggedIn.observeAsState()
    val loginFailed by viewModel.loginFailed.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            // Navigate to the library screen if the user has logged in
            navController.navigate("library")
        }
    }

    PopupDialog(
        showDialog = loginFailed,
        title = "Login Error",
        content = errorMessage
    ) {
        viewModel.loginFailed.value = false
        viewModel.errorMessage.value = null
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

            Spacer(modifier = Modifier.weight(1f))

            Column(
                Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "TaleCraft Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(128.dp)
                )
                Text(
                    text = "TaleCraft",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "It's story time!",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.weight(0.25f))

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp)
            ) {
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
                TextField(
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

                        // Attempt to log in with the provided credentials
                        viewModel.loginUser(username, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "Log in")
                }
                FilledTonalButton(
                    onClick = {
                        navController.navigate("signup")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Need an account?")
                }
            }
        }
    }
}