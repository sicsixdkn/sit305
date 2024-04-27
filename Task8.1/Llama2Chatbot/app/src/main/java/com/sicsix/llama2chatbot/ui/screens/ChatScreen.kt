package com.sicsix.llama2chatbot.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.sicsix.llama2chatbot.utility.Utility
import com.sicsix.llama2chatbot.viewmodels.AppViewModel

@Composable
fun ChatScreen(viewModel: AppViewModel = viewModel()) {
    // Local state for the message input field
    var message by rememberSaveable { mutableStateOf("") }

    // Observe the chat entries and awaitingResponse from the ViewModel and collect it as state so compose can react to data changes.
    val chatEntries by viewModel.chatEntries.observeAsState()
    val awaitingResponse by viewModel.awaitingResponse.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (chatEntries == null) {
                return@Column
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                reverseLayout = true
            ) {
                items(chatEntries!!) { entry ->
                    ChatBubble(
                        isUser = false,
                        message = entry.botMessage
                    )
                    if (entry.userMessage.isNotEmpty()) {
                        ChatBubble(
                            isUser = true,
                            message = entry.userMessage
                        )
                    }
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = message,
                onValueChange = {
                    message = it
                    Utility.validateEntry(it)
                },
                label = { Text("Chat message") },
                enabled = awaitingResponse == false,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            // Do not send an empty message
                            if (message.isEmpty())
                                return@IconButton

                            // Send the message to the ViewModel and reset the message field
                            viewModel.sendMessage(message)
                            message = ""
                        },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ChatBubble(isUser: Boolean, message: String?) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.65f)
                .align(if (isUser) Alignment.CenterEnd else Alignment.CenterStart),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surface,
                contentColor = if (isUser) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface
            )
        ) {
            if (message == null) {
                CircularProgressIndicator(modifier = Modifier.padding(12.dp, 12.dp))
            } else {
                Text(text = message, modifier = Modifier.padding(12.dp, 12.dp))
            }
        }
    }
}