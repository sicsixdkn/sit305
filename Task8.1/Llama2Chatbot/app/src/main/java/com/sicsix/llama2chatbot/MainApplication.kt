package com.sicsix.llama2chatbot

import android.app.Application
import com.sicsix.llama2chatbot.utility.UserPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onTerminate() {
        super.onTerminate()
        // Clear user preferences when the app is terminated
        userPreferences.clearUser()
    }
}