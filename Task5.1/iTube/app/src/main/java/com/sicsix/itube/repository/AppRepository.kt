package com.sicsix.itube.repository

import android.content.Context
import com.sicsix.itube.database.AppDatabase
import com.sicsix.itube.model.PlaylistEntry
import com.sicsix.itube.model.User
import com.sicsix.itube.utility.Utility
import com.sicsix.itube.utility.Utility.Companion.hashPassword

class AppRepository(context: Context) {
    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val playlistEntryDao = AppDatabase.getDatabase(context).playlistEntryDao()

    /**
     * Registers a new user with the given username, password, and full name.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param fullName The full name of the new user.
     * @return The newly registered user, or null if the username is already taken.
     */
    suspend fun registerUser(username: String, password: String, fullName: String): User? {
        val hashedPassword = hashPassword(password)
        val user = User(username = username, password = hashedPassword, fullName = fullName)
        val userId = userDao.insertUser(user).toInt()
        return userDao.getUserById(userId)
    }

    /**
     * Logs in a user with the given username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The user if the login was successful, or null if the username or password is incorrect.
     */
    suspend fun loginUser(username: String, password: String): User? {
        val user = userDao.getUserByUsername(username) ?: return null
        val hashedPassword = hashPassword(password)
        if (user.password == hashedPassword)
            return user
        return null
    }

    /**
     * Inserts a new playlist entry for the user with the given user ID.
     *
     * @param userId The ID of the user to insert the playlist entry for.
     * @param videoUrl The URL of the video to insert into the playlist.
     * @return True if the playlist entry was inserted successfully, false otherwise.
     */
    suspend fun insertPlaylistEntry(userId: Int, videoUrl: String): Boolean {
        val videoId = Utility.extractVideoId(videoUrl) ?: return false
        val playlistEntry = PlaylistEntry(userId = userId, videoUrl = videoUrl, videoId = videoId)
        playlistEntryDao.insertEntry(playlistEntry)
        return true
    }

    /**
     * Gets the playlist entries for the user with the given user ID.
     *
     * @param userId The ID of the user to get the playlist entries for.
     * @return The list of playlist entries for the user.
     */
    suspend fun getPlaylistForUser(userId: Int): List<PlaylistEntry> =
        playlistEntryDao.getEntriesForUser(userId)
}

