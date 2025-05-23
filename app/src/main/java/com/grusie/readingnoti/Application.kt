package com.grusie.readingnoti

import android.app.Application
import com.google.firebase.FirebaseApp
import com.grusie.core.utils.LoggerProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        LoggerProvider.logger = Logger(isDebug = BuildConfig.DEBUG)
        FirebaseApp.initializeApp(this)
    }
}