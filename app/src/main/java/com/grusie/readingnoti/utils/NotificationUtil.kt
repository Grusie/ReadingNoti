package com.grusie.readingnoti.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.grusie.domain.data.tts.TTS_STATE
import com.grusie.presentation.MainActivity
import com.grusie.readingnoti.R

class NotificationUtil {
    companion object {
        private const val CHANNEL_ID = "READING_NOTI_CHANNEL_ID"    // 노티피케이션은 한 가지만 존재할 것이기에 상수로 지정
        const val TTS_SERVICE_ID = 1000     // 포그라운드 서비스를 실행할 때 사용하는 노티피케이션 아이디

        fun getNotificationManager(context: Context): NotificationManager? {
            return context.getSystemService(NotificationManager::class.java)
        }

        fun createNotification(context: Context): NotificationCompat.Builder {
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            val notiManager = getNotificationManager(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notiManager?.run {
                    val foreChannel = NotificationChannel(
                        CHANNEL_ID,
                        context.packageManager?.getPackageInfo(
                            context.packageName,
                            0
                        )?.applicationInfo?.loadLabel(
                            context.packageManager
                        ),
                        NotificationManager.IMPORTANCE_LOW //중요도. 높을수록 사용자에게 알리는 강도가 높아짐

                    )
                    createNotificationChannel(foreChannel)
                }
            }

            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.foreground_noti_title))
                .setContentText(context.getString(R.string.foreground_noti_content_none))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
        }

        /**
         * 현재 TTS의 진행 상태를 Notification에 표시하기 위한 함수
         * @param ttsState 현재 진행중인 TTS의 상태
         */
        fun NotificationCompat.Builder?.changeNotificationMsg(
            context: Context,
            ttsState: TTS_STATE = TTS_STATE.NONE
        ){
            val notiManager = getNotificationManager(context)

            this?.setContentText(ttsState.getNotiMsgByState(context, ttsState = ttsState))
            notiManager?.notify(TTS_SERVICE_ID, this?.build())
        }

        fun rePostNotification(context: Context) {
            val notification = createNotification(context).build()
            val notificationManager = getNotificationManager(context)
            notificationManager?.notify(TTS_SERVICE_ID, notification)
        }
    }
}