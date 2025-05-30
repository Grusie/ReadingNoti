package com.grusie.readingnoti.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import com.grusie.core.utils.LoggerInterface
import com.grusie.domain.data.tts.NotificationData
import com.grusie.domain.data.tts.TTS_STATE
import com.grusie.readingnoti.di.TTSServiceEntryPoint
import com.grusie.readingnoti.utils.NotificationUtil
import com.grusie.readingnoti.utils.NotificationUtil.Companion.changeNotificationMsg
import com.grusie.readingnoti.utils.TTSUtil
import dagger.hilt.android.EntryPointAccessors
import java.util.Locale

/**
 * 알림을 실질적으로 TTS로 읽어주는 서비스
 */
class NotificationTTSService : Service(), TextToSpeech.OnInitListener {
    companion object {
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
    private lateinit var logger: LoggerInterface

    private var isSpeaking = false

    override fun onCreate() {
        super.onCreate()

        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, TTSServiceEntryPoint::class.java)
        logger = entryPoint.logger()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notiBuilder = NotificationUtil.createNotification(this)
        notiManager = NotificationUtil.getNotificationManager(this)

        startForeground(NotificationUtil.TTS_SERVICE_ID, notiBuilder?.build())

        notiTypeId = intent?.getIntExtra(EXTRA_NOTI_TYPE_ID, -1) ?: -1
        if(notiTypeId == -1) {
            return START_NOT_STICKY
        }

        val title = intent?.getStringExtra(EXTRA_NOTIFICATION_TITLE) ?: ""
        val subTitle = intent?.getStringExtra(EXTRA_NOTIFICATION_SUB_TITLE) ?: ""
        val content = intent?.getStringExtra(EXTRA_NOTIFICATION_CONTENT) ?: ""

        notificationData = NotificationData(notiTypeId!!, title, subTitle, content, TTS_STATE.NONE)

        if (!::tts.isInitialized) {
            try {
                tts = TextToSpeech(this, this)
            } catch (e: Exception) {
                logger.e("${this.javaClass.simpleName}, initTTS Error", "${e.message}")
            }
        } else {
            // 이미 초기화된 상태면 알림만 업데이트하고 다시 말하기
            notiBuilder?.changeNotificationMsg(this@NotificationTTSService, TTS_STATE.NONE)
            speakNotification()
        }

        return START_STICKY
    }

    private fun speakNotification() {
        if (notificationData == null || notiTypeId == null || isSpeaking) return

        TTSUtil.speakContent(tts, notiTypeId!!, notificationData!!.content)

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(p0: String?) {
                notiBuilder?.changeNotificationMsg(this@NotificationTTSService, TTS_STATE.SPEAKING)
            }

            override fun onDone(p0: String?) {
                notiBuilder?.changeNotificationMsg(this@NotificationTTSService, TTS_STATE.NONE)
            }

            override fun onError(p0: String?) {
                logger.e("${this.javaClass.simpleName}, TTS speaking Error", "$p0")
                notiBuilder?.changeNotificationMsg(this@NotificationTTSService, TTS_STATE.ERROR)
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.KOREA)

            if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                logger.e("${this.javaClass.simpleName}, TTS onInit Error", "Language is Not Supported")
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
}