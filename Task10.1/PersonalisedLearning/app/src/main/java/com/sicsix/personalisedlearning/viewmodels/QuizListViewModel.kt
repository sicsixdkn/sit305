package com.sicsix.personalisedlearning.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.personalisedlearning.api.RetrofitInstance
import com.sicsix.personalisedlearning.models.Quiz
import com.sicsix.personalisedlearning.models.UserDetails
import com.sicsix.personalisedlearning.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizListViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api

    // LiveData to observe the quizzes
    private val _quizzes = MutableLiveData<List<Quiz>>()
    val quizzes: LiveData<List<Quiz>> get() = _quizzes

    init {
        // Load quizzes when the view model is created
        viewModelScope.launch {
            // Load quizzes in a loop
            while (true) {
                loadQuizzes()
                // Delay for 5 seconds before loading quizzes again
                delay(5000)
            }
        }
    }

    /**
     * Loads the quizzes.
     */
    private suspend fun loadQuizzes() {
        // Get the quizzes and sort them by completion status
        val response = api.getQuizzes("Bearer ${userPreferences.getJWTToken()}")
        if (response.isSuccessful && response.body() != null) {
            _quizzes.value = response.body()?.quizzes?.sortedBy { it.complete }
        }
    }

    /**
     * Returns the user details from the user preferences.
     */
    fun getUserDetails(): UserDetails? {
        return userPreferences.getUserDetails()
    }
}