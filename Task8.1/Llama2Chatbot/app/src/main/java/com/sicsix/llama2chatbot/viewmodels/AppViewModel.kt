package com.sicsix.llama2chatbot.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sicsix.llama2chatbot.api.RetrofitInstance
import com.sicsix.llama2chatbot.model.ChatEntry
import com.sicsix.llama2chatbot.model.ChatRequest
import com.sicsix.llama2chatbot.repository.AppRepository
import com.sicsix.llama2chatbot.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val appRepository: AppRepository, private val userPreferences: UserPreferences
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api

    // LiveData to observe the login success
    val loginSuccess = MutableLiveData<Boolean>()

    // The user details
    val user = userPreferences.getUser()!!

    // LiveData to observe the chat entries
    val chatEntries = appRepository.getChatEntries(user.id).asLiveData()

    // LiveData to observe the response from the chatbot
    val awaitingResponse = MutableLiveData(false)

    /**
     * Login the user with the given name.
     * If the user does not exist, create a new user.
     *
     * @param name The name of the user.
     * @return True if the user was successfully logged in, false otherwise.
     */
    fun login(name: String) {
        viewModelScope.launch {
            loginSuccess.value = appRepository.login(name)
        }
    }

    /**
     * Sets the login success value.
     *
     * @param value The value to set.
     */
    fun setLoginSuccess(value: Boolean) {
        loginSuccess.value = value
    }

    /**
     * Sends a message to the chatbot.
     *
     * @param message The message to send.
     */
    fun sendMessage(message: String) {
        viewModelScope.launch {
            // Set the awaiting response flag to true
            awaitingResponse.value = true

            // Get the chat entries for the user (excluding the most recent message)
            val entries: List<ChatEntry> = chatEntries.value ?: emptyList()

            // Insert the chat entry into the database
            val chatEntryId = appRepository.insertChatEntry(user.id, message)

            // Create a chat request with the message and chat history
            val chatRequest = ChatRequest(message, entries)

            // Make a network request to send the chat request
            val response = api.postChat(chatRequest)

            // Update the chat entry with the response message
            if (response.isSuccessful && response.body() != null) {
                appRepository.updateChatEntry(chatEntryId.toInt(), response.body()!!.message)
            }

            // Set the awaiting response flag to false
            awaitingResponse.value = false
        }
    }
}