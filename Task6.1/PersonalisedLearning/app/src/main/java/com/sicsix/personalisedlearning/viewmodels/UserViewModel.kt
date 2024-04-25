package com.sicsix.personalisedlearning.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.personalisedlearning.api.RetrofitInstance
import com.sicsix.personalisedlearning.models.LoginResponse
import com.sicsix.personalisedlearning.models.UserInterests
import com.sicsix.personalisedlearning.models.UserLogin
import com.sicsix.personalisedlearning.models.UserRegistration
import com.sicsix.personalisedlearning.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api

    // LiveData to observe the login success
    val loginSuccess = MutableLiveData<Boolean>()
    val loginFailed = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    // LiveData to observe the interests
    val interests = MutableLiveData<List<String>>()

    init {
        // Load interests when the view model is created
        viewModelScope.launch {
            val response = api.getInterests()
            if (response.isSuccessful && response.body() != null) {
                interests.value = response.body()?.interests
            } else {
                errorMessage.value = response.errorBody()?.string()
            }
        }
    }

    /**
     * Logs in the user with the given username and password.
     */
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            // Make a network request to login the user
            val response = api.loginUser(UserLogin(username, password))
            handleUserLogin(response)
        }
    }

    /**
     * Registers a new user with the given username, email, password, and phone number.
     */
    fun registerUser(username: String, email: String, password: String, phoneNumber: String) {
        viewModelScope.launch {
            // Make a network request to register the user
            val loginResponse = api.registerUser(UserRegistration(username, email, password, phoneNumber))
            handleUserLogin(loginResponse)
        }
    }

    /**
     * Updates the user interests with the selected interests.
     */
    fun updateInterests(selectedInterests: List<String>) {
        // Check if the JWT token is present
        if (!checkToken())
            return
        viewModelScope.launch {
            // Update the user interests
            api.updateUserInterests("Bearer ${userPreferences.getJWTToken()}", UserInterests(selectedInterests))
        }
    }

    /**
     * Handles the response from the login or registration network request.
     */
    private fun handleUserLogin(response: Response<LoginResponse>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.access_token.isNotEmpty()) {
                // Save the JWT token and user details in the shared preferences
                loginSuccess.value = true
                loginFailed.value = false
                userPreferences.setJWTToken(body.access_token)
                userPreferences.setUserDetails(body.user)
            } else {
                // If the login failed, clear the JWT token and user details
                loginFailed.value = true
                errorMessage.value = body?.msg
                userPreferences.clearJWTToken()
                userPreferences.clearUserDetails()
            }
        } else {
            // If the login failed, clear the JWT token and user details
            loginFailed.value = true
            errorMessage.value = response.errorBody()?.string()
            userPreferences.clearJWTToken()
            userPreferences.clearUserDetails()
        }

    }

    /**
     * Checks if the JWT token is present and returns false if it is not.
     */
    private fun checkToken(): Boolean {
        if (userPreferences.getJWTToken() == null) {
            errorMessage.value = "Token is null"
            return false
        }
        return true
    }
}