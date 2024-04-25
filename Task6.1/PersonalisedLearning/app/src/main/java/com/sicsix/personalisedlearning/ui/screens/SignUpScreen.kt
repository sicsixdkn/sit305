package com.sicsix.personalisedlearning.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.personalisedlearning.utility.Utility.Companion.validateEmail
import com.sicsix.personalisedlearning.utility.Utility.Companion.validateEntry
import com.sicsix.personalisedlearning.utility.Utility.Companion.validatePhoneNumber
import com.sicsix.personalisedlearning.viewmodels.UserViewModel

@Composable
fun SignUpScreen(navController: NavController, viewModel: UserViewModel = viewModel()) {
    // Local state for the username and password input fields
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var confirmEmail by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var phone by rememberSaveable { mutableStateOf("") }

    // Local state for the error messages
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }

    // Observing the LiveData of login result from the ViewModel and collecting it as state so compose can react to data changes.
    val loginSuccess by viewModel.loginSuccess.observeAsState()
    val loginFailed by viewModel.loginFailed.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            navController.navigate("interests")
        }
    }

    PopupDialog(
        showDialog = loginFailed,
        title = "Signup Error",
        content = errorMessage
    ) {
        viewModel.loginFailed.value = false
        viewModel.errorMessage.value = null
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

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp)
            ) {
                Text(
                    text = "Lets get you",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "Setup!",
                    style = MaterialTheme.typography.displayMedium
                )
            }

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Account",
                modifier = Modifier.size(112.dp)
            )

            Column(Modifier.padding(0.dp, 12.dp)) {
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
                        if (usernameError != null) {
                            Text(usernameError!!)
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = {
                        email = it
                        validateEmail(it)
                    },
                    label = { Text(text = "Email") },
                    singleLine = true,
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(emailError!!)
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmEmail,
                    onValueChange = {
                        confirmEmail = it
                        validateEmail(it)
                    },
                    label = { Text(text = "Confirm Email") },
                    singleLine = true,
                    supportingText = {}
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
                        if (passwordError != null) {
                            Text(passwordError!!)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, "Toggle Visibility")
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, "Toggle Visibility")
                        }
                    },
                    supportingText = {}
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = phone,
                    onValueChange = {
                        phone = it
                        validatePhoneNumber(it)
                    },
                    label = { Text(text = "Phone Number") },
                    singleLine = true,
                    isError = phoneError != null,
                    supportingText = {
                        if (phoneError != null) {
                            Text(phoneError!!)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        usernameError = validateEntry(username)
                        passwordError = validateEntry(password)
                        emailError = validateEmail(email)
                        phoneError = validatePhoneNumber(phone)

                        if (usernameError != null || passwordError != null || emailError != null || phoneError != null) {
                            return@Button
                        }

                        if (password != confirmPassword) {
                            passwordError = "Passwords do not match"
                            return@Button
                        }

                        if (email != confirmEmail) {
                            emailError = "Emails do not match"
                            return@Button
                        }

                        viewModel.registerUser(username, email, password, phone)
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth()
                ) {
                    Text(text = "Create account")
                }
            }
        }
    }
}