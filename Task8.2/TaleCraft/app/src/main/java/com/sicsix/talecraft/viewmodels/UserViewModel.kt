package com.sicsix.talecraft.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.talecraft.api.RetrofitInstance
import com.sicsix.talecraft.models.dtos.LoginRequest
import com.sicsix.talecraft.models.dtos.LoginResponse
import com.sicsix.talecraft.models.dtos.RegistrationRequest
import com.sicsix.talecraft.utility.UserPreferences
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
    val isLoggedIn = userPreferences.isLoggedIn
    val loginFailed = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    /**
     * Logs in the user with the given username and password.
     */
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            // Make a network request to login the user
            val response = api.loginUser(LoginRequest(username, password))
            handleUserLogin(response)
        }
    }

    /**
     * Registers a new user with the given username, email, password, and phone number, and logs in the user.
     */
    fun registerUser(username: String, email: String, password: String, phoneNumber: String) {
        viewModelScope.launch {
            // Make a network request to register the user
            val loginResponse = api.registerUser(RegistrationRequest(username, email, password, phoneNumber))
            handleUserLogin(loginResponse)
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
     * Logs out the user by clearing the JWT token and user details.
     */
    fun logout() {
        userPreferences.clearJWTToken()
        userPreferences.clearUserDetails()
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