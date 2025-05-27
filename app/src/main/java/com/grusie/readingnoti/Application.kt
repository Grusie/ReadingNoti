package com.grusie.readingnoti

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.google.firebase.FirebaseApp
import com.grusie.core.utils.LoggerProvider
import com.grusie.readingnoti.service.NotificationReceiverService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        LoggerProvider.logger = Logger(isDebug = BuildConfig.DEBUG)
        FirebaseApp.initializeApp(this)
        rebindNotificationListenerService(this)
    }

    private fun rebindNotificationListenerService(context: Context) {
        val cn = ComponentName(context, NotificationReceiverService::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            cn,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            cn,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}