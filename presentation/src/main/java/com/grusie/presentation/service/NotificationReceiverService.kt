package com.grusie.presentation.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.content.ContextCompat
import com.grusie.presentation.data.GlobalDataStore

/**
 * 알림을 받아서 해당 데이터를 TTS를 처리하는 포그라운드 서비스를 실행시키는 서비스
 */
class NotificationReceiverService : NotificationListenerService() {
    // 알림이 들어오면 무조건 동작하는 함수
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.packageName?.let { pn ->

            // TODO: 사용자가 선택한 앱 종류로 변경되어야 함
            val notiTypeList = GlobalDataStore.getNotiTypeList()
            if (notiTypeList.isEmpty()) {
                return
            }

            val notiType = notiTypeList.firstOrNull { it.packageName == pn } ?: run { return }

            val intent = Intent(this, NotificationTTSService::class.java).apply {
                val notificationExtras = sbn.notification.extras

                val title = notificationExtras?.getString(Notification.EXTRA_TITLE) ?: ""
                val subTitle = notificationExtras?.getString(Notification.EXTRA_SUB_TEXT) ?: ""
                val content = notificationExtras?.getString(Notification.EXTRA_TEXT) ?: ""

                putExtra(NotificationTTSService.EXTRA_NOTIFICATION_TITLE, title)
                putExtra(NotificationTTSService.EXTRA_NOTIFICATION_SUB_TITLE, subTitle)
                putExtra(NotificationTTSService.EXTRA_NOTIFICATION_CONTENT, content)
                putExtra(NotificationTTSService.EXTRA_NOTI_TYPE_ID, notiType.id)
            }

            ContextCompat.startForegroundService(this, intent)
        }
        super.onNotificationPosted(sbn)
    }
}