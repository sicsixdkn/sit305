package com.sicsix.lostandfound

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the Places API using a key stored outside source control
        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
    }
}