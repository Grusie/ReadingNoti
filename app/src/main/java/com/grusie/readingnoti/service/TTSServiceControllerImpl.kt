package com.grusie.readingnoti.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.grusie.core.common.TTSServiceController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TTSServiceControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): TTSServiceController {
    override fun startService() {
        val intent = Intent(context, NotificationTTSService::class.java)
        ContextCompat.startForegroundService(context, intent)
        if(!NotificationListenerServiceState.isListenerConnect) {
            rebindNotificationListenerService(context)
        }
    }

    override fun stopService() {
        val intent = Intent(context, NotificationTTSService::class.java)
        context.stopService(intent)
    }

    /**
     * TTSService를 실행 시킬 때, NotificationReceiverService가 바인딩 되어 있지 않다면 바인딩을 시도
     */
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