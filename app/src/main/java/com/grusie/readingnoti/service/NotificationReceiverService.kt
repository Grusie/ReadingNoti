package com.grusie.readingnoti.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.content.ContextCompat
import com.grusie.core.common.SettingType
import com.grusie.core.utils.LoggerInterface
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.readingnoti.SettingObserveManager
import com.grusie.readingnoti.di.NotiRecvServiceEntryPoint
import com.grusie.readingnoti.utils.NotificationUtil
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 알림을 받아서 해당 데이터를 TTS를 처리하는 포그라운드 서비스를 실행시키는 서비스
 */
class NotificationReceiverService : NotificationListenerService() {
    private val mergedGeneralSettingMap: MutableMap<Int, MergedSetting> = mutableMapOf()
    private val mergedAppSettingMap: MutableMap<Int, MergedSetting> = mutableMapOf()

    private lateinit var logger: LoggerInterface
    private lateinit var totalSettingUseCases: TotalSettingUseCases

    private var job: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, NotiRecvServiceEntryPoint::class.java)
        logger = entryPoint.logger()
        totalSettingUseCases = entryPoint.totalSettingUseCases()

        observeMergedSettings()
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

            if(packageName == getPackageName() && notificationId == NotificationUtil.TTS_SERVICE_ID) {
                NotificationUtil.rePostNotification(this)
            }
        }
        super.onNotificationRemoved(sbn)
    }

    // 알림이 들어오면 무조건 동작하는 함수
    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        sbn?.packageName?.let { pn ->
            var currentAppSetting: DomainPersonalSettingDto? = null
            for (mergedAppSettingMap in mergedAppSettingMap.values) {
                if(mergedAppSettingMap.totalSetting.packageName == pn) {
                    currentAppSetting = mergedAppSettingMap.personalSetting
                    break
                }
            }

            // 패키지명에 부합하는 설정이 없을 경우 or 해당 앱 알림 설정이 꺼져있을 경우
            if(currentAppSetting == null || !currentAppSetting.isEnabled) {
                return
            }

            logger.i("${this::class.simpleName}", "StatusBarNotification : $sbn")
            logger.i("${this::class.simpleName}", "Using Notification Data : ${sbn.notification?.extras}")

            val intent = Intent(this, NotificationTTSService::class.java).apply {
                val notificationExtras = sbn.notification.extras

                val title = notificationExtras?.getString(Notification.EXTRA_TITLE) ?: ""
                val subTitle = notificationExtras?.getString(Notification.EXTRA_SUB_TEXT) ?: ""
                val content = notificationExtras?.getString(Notification.EXTRA_TEXT) ?: ""

                putExtra(NotificationTTSService.EXTRA_NOTIFICATION_TITLE, title)
                putExtra(NotificationTTSService.EXTRA_NOTIFICATION_SUB_TITLE, subTitle)
                putExtra(NotificationTTSService.EXTRA_NOTIFICATION_CONTENT, content)
                putExtra(NotificationTTSService.EXTRA_NOTI_TYPE_ID, currentAppSetting.menuId)
            }

            ContextCompat.startForegroundService(this, intent)
        }
        super.onNotificationPosted(sbn)
    }

    private fun observeMergedSettings() {
        if (job?.isActive == true) return

        job = serviceScope.launch {
            SettingObserveManager.mergedSettingMap.collect { mergedMap ->
                // 기존 데이터 클리어 후 업데이트
                mergedGeneralSettingMap.clear()
                mergedAppSettingMap.clear()

                mergedMap.forEach { (menuId, mergedSetting) ->
                    if (mergedSetting.totalSetting.type == SettingType.GENERAL) {
                        mergedGeneralSettingMap[menuId] = mergedSetting
                    } else {
                        mergedAppSettingMap[menuId] = mergedSetting
                    }
                }

                logger.d("${this@NotificationReceiverService::class.simpleName}", "MergedSettings updated")
            }
        }
    }
}