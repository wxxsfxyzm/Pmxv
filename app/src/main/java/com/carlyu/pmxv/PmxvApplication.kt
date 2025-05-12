package com.carlyu.pmxv

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class PmxvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any libraries or SDKs here
        // For example, if you're using Timber for logging:
        if (BuildConfig.DEBUG) { // Only plant the debug tree in debug builds
            Timber.plant(Timber.DebugTree())
        }
    }
}