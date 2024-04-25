package com.sicsix.personalisedlearning.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.personalisedlearning.viewmodels.UserViewModel

@Composable
fun InterestsScreen(navController: NavController, viewModel: UserViewModel = viewModel()) {
    // Collecting state from the ViewModel
    val interests by viewModel.interests.observeAsState(emptyList())
    // Initialize selectedInterests with an empty list
    val selectedInterests = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 12.dp)
            ) {
                Text(
                    text = "Your",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "Interests",
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = "You may select up to 10 topics",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(0.dp, 8.dp)
                )
            }

            LazyColumn(Modifier.fillMaxWidth()) {
                items(interests.chunked(2)) { pair ->
                    Row(Modifier.fillMaxWidth()) {
                        pair.forEach { interest ->
                            OutlinedButton(
                                onClick = {
                                    if (selectedInterests.contains(interest)) {
                                        selectedInterests.remove(interest)
                                    } else {
                                        if (selectedInterests.size < 10) {
                                            selectedInterests.add(interest)
                                        }
                                    }
                                },
                                enabled = selectedInterests.size < 10 || selectedInterests.contains(interest),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(interest.length.toFloat()),
                                colors = if (selectedInterests.contains(interest)) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = colorScheme.primary,
                                        contentColor = colorScheme.onPrimary
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = colorScheme.onPrimary,
                                        contentColor = colorScheme.primary
                                    )
                                }
                            ) {
                                Text(text = interest, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateInterests(selectedInterests)
                    navController.navigate("quizlist")
                },
                enabled = selectedInterests.size > 0,
                modifier = Modifier
                    .align(Alignment.End)
                    .fillMaxWidth()
                    .padding(24.dp, 12.dp)
            ) {
                Text(text = "Next")
            }
        }
    }
}