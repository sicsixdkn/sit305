package com.sicsix.personalisedlearning.utility

import android.content.Context
import com.google.gson.Gson
import com.sicsix.personalisedlearning.models.UserDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()


    fun setJWTToken(jwtToken: String) {
        with(sharedPreferences.edit()) {
            putString("jwt_token", jwtToken)
            apply()
        }
    }

    fun getJWTToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun clearJWTToken() {
        with(sharedPreferences.edit()) {
            remove("jwt_token")
            apply()
        }
    }

    fun setUserDetails(userDetails: UserDetails) {
        val userDetailsJson = gson.toJson(userDetails)
        with(sharedPreferences.edit()) {
            putString("user_details", userDetailsJson)
            apply()
        }
    }

    fun getUserDetails(): UserDetails? {
        val userDetailsJson = sharedPreferences.getString("user_details", null)
        return if (userDetailsJson != null) {
            gson.fromJson(userDetailsJson, UserDetails::class.java)
        } else {
            null
        }
    }

    fun clearUserDetails() {
        with(sharedPreferences.edit()) {
            remove("user_details")
            apply()
        }
    }
}