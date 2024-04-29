package com.sicsix.personalisedlearning.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.personalisedlearning.api.RetrofitInstance
import com.sicsix.personalisedlearning.models.UserDetails
import com.sicsix.personalisedlearning.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api

    // LiveData to observe the stats
    val totalQuestions = MutableLiveData<Int>()
    val correctAnswers = MutableLiveData<Int>()
    val quizzesReady = MutableLiveData<Int>()

    /**
     * Returns the user details from the user preferences.
     */
    fun getUserDetails(): UserDetails? {
        return userPreferences.getUserDetails()
    }

    /**
     * Gets the stats for the user.
     */
    fun getStats() {
        viewModelScope.launch {
            val response = api.getStats("Bearer ${userPreferences.getJWTToken()}")
            if (response.isSuccessful && response.body() != null) {
                totalQuestions.value = response.body()?.total_questions
                correctAnswers.value = response.body()?.correct_answers
                quizzesReady.value = response.body()?.quizzes_ready
            }
        }
    }
}