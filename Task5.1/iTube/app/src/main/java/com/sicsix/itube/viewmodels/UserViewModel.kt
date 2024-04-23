package com.sicsix.itube.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sicsix.itube.model.PlaylistEntry
import com.sicsix.itube.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    // Repository to interact with the database
    private val repository: AppRepository

    // LiveData to observe the login success
    private val _loginSuccess = MutableLiveData<Boolean?>()
    val loginSuccess: LiveData<Boolean?> = _loginSuccess

    // LiveData to observe the user ID
    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> = _userId

    // LiveData to observe the playlist entries
    private val _playlistEntries = MutableLiveData<List<PlaylistEntry>>()
    val playlistEntries: LiveData<List<PlaylistEntry>> = _playlistEntries

    init {
        repository = AppRepository(application)
    }

    /**
     * Registers a new user with the given username, password, and full name.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param fullName The full name of the new user.
     */
    fun register(username: String, password: String, fullName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.registerUser(username, password, fullName)
        }
    }

    /**
     * Logs in a user with the given username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.loginUser(username, password)
            if (user != null) {
                _userId.postValue(user.userId)
                _loginSuccess.postValue(true)
            } else {
                _loginSuccess.postValue(false)
            }
        }
    }

    /**
     * Inserts a new playlist entry for the user with the given user ID.
     *
     * @param videoUrl The URL of the video to insert into the playlist.
     */
    fun insertPlaylistEntry(videoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (userId.value == null)
                return@launch

            repository.insertPlaylistEntry(userId = userId.value!!, videoUrl = videoUrl)
        }
    }

    /**
     * Loads the playlist entries for the current user.
     */
    fun loadPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            userId.value?.let {
                _playlistEntries.postValue(repository.getPlaylistForUser(it))
            }
        }
    }

    /**
     * Resets the login success LiveData.
     */
    fun resetLoginSuccess() {
        _loginSuccess.value = null
    }
}