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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sicsix.itube.utility.Utility.Companion.validateEntry
import com.sicsix.itube.viewmodels.UserViewModel

@Composable
fun SignUpScreen(navController: NavController, viewModel: UserViewModel) {
    // Local state for the username and password input fields
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Local state for the error messages
    var fullNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullName,
                onValueChange = {
                    fullName = it
                    validateEntry(it)
                },
                label = { Text(text = "Full Name") },
                singleLine = true,
                isError = fullNameError != null,
                supportingText = {
                    if (fullNameError == null) {
                        Text("")
                    } else {
                        Text(fullNameError!!)
                    }
                },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = {
                    username = it
                    validateEntry(it)
                },
                label = { Text(text = "Username") },
                singleLine = true,
                isError = usernameError != null,
                supportingText = {
                    if (usernameError == null) {
                        Text("")
                    } else {
                        Text(usernameError!!)
                    }
                },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
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
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    validateEntry(it)
                },
                label = { Text(text = "Confirm Password") },
                singleLine = true,
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
                    // Validate the input fields before attempting to register
                    fullNameError = validateEntry(fullName)
                    usernameError = validateEntry(username)
                    passwordError = validateEntry(password)
                    if (fullNameError != null || usernameError != null || passwordError != null) {
                        return@Button
                    }
                    if (password != confirmPassword) {
                        passwordError = "Passwords do not match"
                        return@Button
                    }

                    // Register the user and navigate to the login screen
                    viewModel.register(username, password, fullName)
                    navController.navigate("login")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Account")
            }
        }
    }
}