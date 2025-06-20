package com.grusie.readingnoti.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.content.ContextCompat
import com.grusie.core.common.TotalMenu
import com.grusie.core.utils.LoggerInterface
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.tts.NotificationData
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.presentation.utils.SettingObserveManager
import com.grusie.readingnoti.di.NotiRecvServiceEntryPoint
import com.grusie.readingnoti.utils.NotificationUtil
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * 알림을 받아서 해당 데이터를 처리하는 메인 포그라운드 서비스를 실행시키는 서비스
 */
class NotificationReceiverService : NotificationListenerService() {
    private lateinit var logger: LoggerInterface
    private var appSettingJob: Job? = null
    private var generalSettingJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentAppSettings: Map<Int, MergedSetting> = emptyMap()
    private var currentGeneralSettings: Map<Int, MergedSetting> = emptyMap()
    private var isCollectNotiEnabled: Boolean = false

    override fun onCreate() {
        super.onCreate()

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            NotiRecvServiceEntryPoint::class.java
        )
        logger = entryPoint.logger()

        observeAppSettings()
        observeGeneralSettings()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        logger.d("${this::class.simpleName}", "notificationReceiverService Connected")
        NotificationListenerServiceState.isListenerConnect = true
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        logger.d("${this::class.simpleName}", "notificationReceiverService Disconnected")
        NotificationListenerServiceState.isListenerConnect = false
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            val notificationId = it.id
            val packageName = it.packageName
            if (isCollectNotiEnabled && packageName == getPackageName() && notificationId == NotificationUtil.TTS_SERVICE_ID) {
                NotificationUtil.rePostNotification(this)
            }
        }
        super.onNotificationRemoved(sbn)
    }

    // 알림이 들어오면 무조건 동작하는 함수
    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        // 알림 수집 설정이 꺼져있다면 리턴
        if (!isCollectNotiEnabled) return

        sbn?.packageName?.let { packageName ->
            var currentAppSetting: DomainPersonalSettingDto? = null
            for (mergedAppSetting in currentAppSettings.values) {
                if (mergedAppSetting.totalSetting.packageName == packageName) {
                    currentAppSetting = mergedAppSetting.personalSetting
                    break
                }
            }

            // 패키지명에 부합하는 설정이 없을 경우 or 해당 앱 알림 설정이 꺼져있을 경우
            if (currentAppSetting == null || !currentAppSetting.isEnabled) {
                return
            }

            logger.i("${this::class.simpleName}", "StatusBarNotification : $sbn")
            logger.i(
                "${this::class.simpleName}",
                "Using Notification Data : ${sbn.notification?.extras.toString()}"
            )

            val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = sbn.notification.channelId
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.getNotificationChannel(channelId)?.importance
                    ?: NotificationManager.IMPORTANCE_DEFAULT
            } else {
                when (sbn.notification.priority) {
                    Notification.PRIORITY_MAX, Notification.PRIORITY_HIGH -> NotificationManager.IMPORTANCE_HIGH
                    Notification.PRIORITY_DEFAULT -> NotificationManager.IMPORTANCE_DEFAULT
                    Notification.PRIORITY_LOW -> NotificationManager.IMPORTANCE_LOW
                    Notification.PRIORITY_MIN -> NotificationManager.IMPORTANCE_MIN
                    else -> NotificationManager.IMPORTANCE_DEFAULT
                }
            }

            val intent = Intent(this, MainService::class.java).apply {
                val notificationExtras = sbn.notification.extras

                val title = notificationExtras?.getString(Notification.EXTRA_TITLE) ?: ""
                val subTitle = notificationExtras?.getString(Notification.EXTRA_SUB_TEXT) ?: ""
                val content = notificationExtras?.getString(Notification.EXTRA_TEXT) ?: ""

                val notificationData = NotificationData(
                    notiMenuId = currentAppSetting.menuId,
                    packageName = packageName,
                    importance = importance,
                    channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) sbn.notification.channelId else "",
                    title = title,
                    subTitle = subTitle,
                    content = content,
                    timeStamp = System.currentTimeMillis()
                )

                putExtra(MainService.EXTRA_NOTIFICATION_DATA, notificationData)
            }

            ContextCompat.startForegroundService(this, intent)
        }
        super.onNotificationPosted(sbn)
    }

    private fun observeAppSettings() {
        if (appSettingJob?.isActive == true) return

        appSettingJob = serviceScope.launch {
            SettingObserveManager.mergedAppSettingMap.collect { appSettingMap ->
                currentAppSettings = appSettingMap

                logger.d(
                    "${this@NotificationReceiverService::class.simpleName}",
                    "AppSettingMap updated: size=${appSettingMap.size}"
                )
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun observeGeneralSettings() {
        if (generalSettingJob?.isActive == true) return

        generalSettingJob = serviceScope.launch {
            SettingObserveManager.mergedGeneralSettingMap.collect { generalSettingMap ->
                currentGeneralSettings = generalSettingMap
                isCollectNotiEnabled =
                    generalSettingMap[TotalMenu.COLLECT_NOTI_ENABLED.menuId]?.personalSetting?.isEnabled == true

                logger.d(
                    "${this@NotificationReceiverService::class.simpleName}",
                    "generalSettingMap updated: size=${generalSettingMap.size}"
                )
            }
        }
    }
}