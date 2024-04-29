package com.sicsix.personalisedlearning.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.personalisedlearning.api.RetrofitInstance
import com.sicsix.personalisedlearning.models.Quiz
import com.sicsix.personalisedlearning.models.QuizUpdate
import com.sicsix.personalisedlearning.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api

    // LiveData to observe the quiz
    private val _quiz = MutableLiveData<Quiz>()
    val quiz: LiveData<Quiz> get() = _quiz

    /**
     * Loads the quiz with the given ID.
     */
    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            val response = api.getQuizzes("Bearer ${userPreferences.getJWTToken()}")
            if (response.isSuccessful && response.body() != null) {
                _quiz.value = response.body()?.quizzes?.firstOrNull { it.quiz_id == quizId }
            }
        }
    }
    /**
     * Uploads the selected answers for the quiz.
     */
    suspend fun uploadQuizSelections(quizId: String, selectedAnswers: List<String>): Boolean {
        val response = api.completeQuiz("Bearer ${userPreferences.getJWTToken()}", QuizUpdate(quizId, selectedAnswers))
        return response.isSuccessful
    }
}