package com.grusie.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import com.grusie.presentation.MainActivity
import com.grusie.presentation.R
import com.grusie.presentation.data.NotificationData
import com.grusie.presentation.data.TTS_STATE
import com.grusie.presentation.utils.TTSUtil
import java.util.Locale

/**
 * 알림을 실질적으로 TTS로 읽어주는 서비스
 */
class NotificationTTSService : Service(), TextToSpeech.OnInitListener {
    companion object {
        const val CHANNEL_ID = "READING_NOTI_CHANNEL_ID"    // 노티피케이션은 한 가지만 존재할 것이기에 상수로 지정
        const val SERVICE_ID = 1000     // 포그라운드 서비스를 실행할 때 사용하는 서비스 아이디

        const val EXTRA_NOTI_TYPE_ID = "extra_noti_type_id"
        const val EXTRA_NOTIFICATION_TITLE = "extra_notification_title"
        const val EXTRA_NOTIFICATION_SUB_TITLE = "extra_notification_sub_title"
        const val EXTRA_NOTIFICATION_CONTENT = "extra_notification_content"
    }

    private lateinit var tts: TextToSpeech      // TTS 객체
    private var notiTypeId: Int? = null     // 알림 타입(카카오톡, 인스타 그램 등)의 아이디
    private var notificationData: NotificationData? = null
    private var notiBuilder: NotificationCompat.Builder? = null
    private var notiManager: NotificationManager? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notiTypeId = intent?.getIntExtra(EXTRA_NOTI_TYPE_ID, -1)

        val title = intent?.getStringExtra(EXTRA_NOTIFICATION_TITLE) ?: ""
        val subTitle = intent?.getStringExtra(EXTRA_NOTIFICATION_SUB_TITLE) ?: ""
        val content = intent?.getStringExtra(EXTRA_NOTIFICATION_CONTENT) ?: ""

        if(notiTypeId == null || notiTypeId == -1) return START_NOT_STICKY

        notificationData = NotificationData(notiTypeId!!, title, subTitle, content, TTS_STATE.NONE)
        notiBuilder = createNotification()

        try {
            startForeground(SERVICE_ID, notiBuilder?.build())
            tts = TextToSpeech(this, this)
        } catch (e: Exception) {
            Log.e("${this.javaClass.simpleName}, initTTS Error", "${e.message}")
        }

        return START_NOT_STICKY
    }

    private fun createNotification(): NotificationCompat.Builder? {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        notiManager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notiManager?.run {
                val foreChannel = NotificationChannel(
                    CHANNEL_ID,
                    packageManager?.getPackageInfo(packageName, 0)?.applicationInfo?.loadLabel(
                        packageManager
                    ),
                    NotificationManager.IMPORTANCE_LOW //중요도. 높을수록 사용자에게 알리는 강도가 높아짐

                )
                createNotificationChannel(foreChannel)
            }
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.foreground_noti_title))
            .setContentText(getString(R.string.foreground_noti_content_none))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
    }

    private fun speakNotification() {
        if (notificationData == null || notiTypeId == null) return

        TTSUtil.speakContent(tts, notiTypeId!!, notificationData!!.content)

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(p0: String?) {
                changeNotificationMsg(TTS_STATE.SPEAKING)
            }

            override fun onDone(p0: String?) {
                changeNotificationMsg(TTS_STATE.NONE)
            }

            override fun onError(p0: String?) {
                Log.e("${this.javaClass.simpleName}, TTS speaking Error", "$p0")
                changeNotificationMsg(TTS_STATE.ERROR)
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.KOREA)

            if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("${this.javaClass.simpleName}, TTS onInit Error", "Language is Not Supported")
            } else {
                speakNotification()
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        super.onDestroy()
    }

    /**
     * 현재 TTS의 진행 상태를 Notification에 표시하기 위한 함수
     * @param ttsState 현재 진행중인 TTS의 상태
     */
    private fun changeNotificationMsg(ttsState: TTS_STATE = TTS_STATE.NONE){
        notificationData = notificationData?.copy(ttsState = ttsState)

        notiBuilder?.setContentText(TTS_STATE.getNotiMsgByState(this@NotificationTTSService, ttsState = ttsState))
        notiManager?.notify(SERVICE_ID, notiBuilder?.build())
    }
}