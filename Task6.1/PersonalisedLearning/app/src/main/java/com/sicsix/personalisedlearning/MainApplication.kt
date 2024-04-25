package com.sicsix.personalisedlearning

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import com.sicsix.personalisedlearning.utility.UserPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onTerminate() {
        super.onTerminate()
        // Clear user preferences when the app is terminated
        userPreferences.clearJWTToken()
        userPreferences.clearUserDetails()
    }
}