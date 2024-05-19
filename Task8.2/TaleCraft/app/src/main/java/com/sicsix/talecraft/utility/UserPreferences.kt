package com.sicsix.talecraft.utility

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sicsix.talecraft.models.UserDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    // Get the shared preferences for the user_preferences file.
    private val sharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Create a MutableLiveData object to store the user's login status.
    private val _isLoggedIn = MutableLiveData<Boolean>()

    // Create a getter for the MutableLiveData object.
    val isLoggedIn: MutableLiveData<Boolean> get() = _isLoggedIn

    /**
     * Sets the JWT token in the shared preferences.
     *
     * @param jwtToken The JWT token to set.
     */
    fun setJWTToken(jwtToken: String) {
        with(sharedPreferences.edit()) {
            putString("jwt_token", jwtToken)
            apply()
        }
        _isLoggedIn.postValue(true)
    }

    /**
     * Gets the JWT token from the shared preferences.
     *
     * @return The JWT token.
     */
    fun getJWTToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    /**
     * Clears the JWT token from the shared preferences, used when the user logs out.
     */
    fun clearJWTToken() {
        with(sharedPreferences.edit()) {
            remove("jwt_token")
            apply()
        }
        _isLoggedIn.postValue(false)
    }

    /**
     * Sets the user details in the shared preferences.
     *
     * @param userDetails The user details to set.
     */
    fun setUserDetails(userDetails: UserDetails) {
        val userDetailsJson = gson.toJson(userDetails)
        with(sharedPreferences.edit()) {
            putString("user_details", userDetailsJson)
            apply()
        }
    }

    /**
     * Gets the user details from the shared preferences.
     *
     * @return The user details.
     */
    fun getUserDetails(): UserDetails? {
        val userDetailsJson = sharedPreferences.getString("user_details", null)
        return if (userDetailsJson != null) {
            gson.fromJson(userDetailsJson, UserDetails::class.java)
        } else {
            null
        }
    }

    /**
     * Clears the user details from the shared preferences, used when the user logs out.
     */
    fun clearUserDetails() {
        with(sharedPreferences.edit()) {
            remove("user_details")
            apply()
        }
    }
}