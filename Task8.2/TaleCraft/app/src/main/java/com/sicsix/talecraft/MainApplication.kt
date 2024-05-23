package com.sicsix.talecraft

import android.app.Application
import com.sicsix.talecraft.utility.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        // Clear user preferences when the app is started
        userPreferences.clearJWTToken()
        userPreferences.clearUserDetails()
    }
}