package com.sicsix.personalisedlearning.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.personalisedlearning.api.RetrofitInstance
import com.sicsix.personalisedlearning.models.AnsweredQuestion
import com.sicsix.personalisedlearning.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api


    // LiveData to observe the answered questions
    private val _answeredQuestions = MutableLiveData<List<AnsweredQuestion>>()
    val answeredQuestions: LiveData<List<AnsweredQuestion>> get() = _answeredQuestions


    /**
     * Loads the answered questions based on the history type (correct/incorrect/all).
     *
     * @param historyType The type of history to load.
     */
    fun loadAnsweredQuestions(historyType: String) {
        viewModelScope.launch {
            val response = when (historyType) {
                "all" -> api.getHistory("Bearer ${userPreferences.getJWTToken()}")
                "correct" -> api.getCorrectHistory("Bearer ${userPreferences.getJWTToken()}")
                "incorrect" -> api.getIncorrectHistory("Bearer ${userPreferences.getJWTToken()}")
                else -> throw IllegalArgumentException("Invalid history type")
            }
            if (response.isSuccessful && response.body() != null) {
                _answeredQuestions.value = response.body()?.questions
            }
        }
    }
}