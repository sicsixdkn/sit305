package com.sicsix.llama2chatbot.utility

import android.content.Context
import com.google.gson.Gson
import com.sicsix.llama2chatbot.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun setUser(userDetails: User) {
        val user = gson.toJson(userDetails)
        with(sharedPreferences.edit()) {
            putString("user", user)
            apply()
        }
    }

    fun getUser(): User? {
        val user = sharedPreferences.getString("user", null)
        return if (user != null) {
            gson.fromJson(user, User::class.java)
        } else {
            null
        }
    }

    fun clearUser() {
        with(sharedPreferences.edit()) {
            remove("user")
            apply()
        }
    }
}