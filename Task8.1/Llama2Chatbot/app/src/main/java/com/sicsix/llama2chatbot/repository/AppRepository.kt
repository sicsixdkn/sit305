package com.sicsix.llama2chatbot.repository

import android.content.Context
import com.sicsix.llama2chatbot.database.AppDatabase
import com.sicsix.llama2chatbot.model.ChatEntry
import com.sicsix.llama2chatbot.model.User
import com.sicsix.llama2chatbot.utility.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val userPreferences: UserPreferences
) {
    private val appDao = AppDatabase.getDatabase(context).appDao()

    /**
     * Login the user with the given name.
     * If the user does not exist, create a new user.
     *
     * @param name The name of the user.
     * @return True if the user was successfully logged in, false otherwise.
     */
    suspend fun login(name: String): Boolean {
        var user = appDao.getUserByName(name)

        if (user == null) {
            val userId = appDao.createUser(User(name = name))
            user = appDao.getUserById(userId)
            appDao.insertChatEntry(
                ChatEntry(
                    userId = userId.toInt(),
                    userMessage = "",
                    botMessage = "Hello ${user!!.name}, how can I help you?",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        userPreferences.setUser(user)
        return true
    }

    /**
     * Gets the chat entries for the user with the given ID.
     *
     * @param userId The ID of the user to get the chat entries for.
     * @return The chat entries for the user with the given ID.
     */
    fun getChatEntries(userId: Int) = appDao.getChatEntries(userId)

    /**
     * Saves a new chat entry with the given details.
     *
     * @param userId The ID of the user who sent the chat entry.
     * @param userMessage The message of the chat entry.
     */
    suspend fun insertChatEntry(userId: Int, userMessage: String): Long {
        return appDao.insertChatEntry(
            ChatEntry(
                userId = userId,
                userMessage = userMessage,
                botMessage = null,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /**
     * Updates the chat entry with the given ID with the bot message.
     *
     * @param chatEntryId The ID of the chat entry to update.
     * @param botMessage The bot message to update the chat entry with.
     */
    suspend fun updateChatEntry(chatEntryId: Int, botMessage: String) {
        val chatEntry = appDao.getChatEntryById(chatEntryId)
        if (chatEntry != null) {
            appDao.updateChatEntry(chatEntryId, botMessage)
        }
    }
}

