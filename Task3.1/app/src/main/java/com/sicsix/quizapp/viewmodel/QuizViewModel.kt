package com.sicsix.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.quizapp.model.Question
import com.sicsix.quizapp.model.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    // Holds the username, initially null, observable by UI
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username

    // Holds the list of questions, initially empty
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    // Holds the current question index the user is on
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    // Holds the user's score
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    // Holds the index of the answer selected by the user, null if no selection
    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex

    // Indicates whether the user has submitted an answer to the current question
    private val _answerSubmitted = MutableStateFlow(false)
    val answerSubmitted: StateFlow<Boolean> = _answerSubmitted

    init {
        // Load questions when ViewModel is created
        loadQuestions()
    }

    // Loads questions asynchronously from the repository
    private fun loadQuestions() {
        viewModelScope.launch {
            _questions.value = QuestionRepository.getRandomQuestions()
        }
    }

    // Sets the username
    fun setUsername(name: String) {
        _username.value = name
    }

    // Selects an answer, ensuring no selection can be made after submission
    fun selectAnswer(index: Int) {
        if (!_answerSubmitted.value)
            _selectedAnswerIndex.value = index
    }

    // Submits the selected answer, checking it against the correct answer
    fun submitAnswer() {
        if (_selectedAnswerIndex.value == null)
            return

        _answerSubmitted.value = true
        _selectedAnswerIndex.value?.let { selectedIndex ->
            val currentQuestion = _questions.value.getOrNull(_currentIndex.value)
            if (currentQuestion != null && selectedIndex == currentQuestion.correctAnswerIndex)
                _score.value += 1
        }
    }

    // Moves to the next question if available, returns false if at the end of the quiz
    fun moveToNextQuestion(): Boolean {
        if (_currentIndex.value < questions.value.size - 1) {
            _currentIndex.value += 1
            _answerSubmitted.value = false
            _selectedAnswerIndex.value = null
            return true
        }
        return false
    }

    // Resets the quiz to initial state and loads new random questions
    fun reset() {
        _currentIndex.value = 0
        _answerSubmitted.value = false
        _selectedAnswerIndex.value = null
        _score.value = 0
        loadQuestions()
    }
}