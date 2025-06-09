package com.grusie.readingnoti.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import com.grusie.core.common.TotalMenu
import com.grusie.core.utils.LoggerInterface
import com.grusie.domain.data.DomainMsgData
import com.grusie.domain.data.tts.NotificationData
import com.grusie.domain.data.tts.TTS_STATE
import com.grusie.domain.usecase.msgData.MsgDataUseCases
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.presentation.utils.SettingObserveManager
import com.grusie.readingnoti.di.MainServiceEntryPoint
import com.grusie.readingnoti.utils.NotificationUtil
import com.grusie.readingnoti.utils.NotificationUtil.Companion.changeNotificationMsg
import com.grusie.readingnoti.utils.TTSUtil
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 알림을 실질적으로 처리하는 서비스
 * 알림을 저장하거나, TTS로 읽어주는 서비스
 */
class MainService : Service(), TextToSpeech.OnInitListener {
    companion object {
        const val EXTRA_NOTI_TYPE_ID = "extra_noti_type_id"
        const val EXTRA_NOTIFICATION_TITLE = "extra_notification_title"
        const val EXTRA_NOTIFICATION_SUB_TITLE = "extra_notification_sub_title"
        const val EXTRA_NOTIFICATION_CONTENT = "extra_notification_content"
        const val EXTRA_NOTIFICATION_TIME_STAMP = "extra_notification_time_stamp"
    }

    private lateinit var tts: TextToSpeech      // TTS 객체
    private var notiTypeId: Int? = null     // 알림 타입(카카오톡, 인스타 그램 등)의 아이디
    private var notificationData: NotificationData? = null
    private var notiBuilder: NotificationCompat.Builder? = null
    private var notiManager: NotificationManager? = null

    private lateinit var logger: LoggerInterface
    private lateinit var msgDataUseCases: MsgDataUseCases

    private var appSettingJob: Job? = null
    private var generalSettingJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentAppSettings: Map<Int, MergedSetting> = emptyMap()
    private var currentGeneralSettings: Map<Int, MergedSetting> = emptyMap()

    private var isSpeaking = false

    override fun onCreate() {
        super.onCreate()

        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, MainServiceEntryPoint::class.java)
        logger = entryPoint.logger()
        msgDataUseCases = entryPoint.msgDataUseCases()

        observeGeneralSettings()
        observeAppSettings()
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
        val timeStamp = intent?.getLongExtra(EXTRA_NOTIFICATION_TIME_STAMP, 0) ?: 0

        serviceScope.launch {
            if(content.isNotEmpty()) {
                // 넘어온 알림은 알림 수집 기능이 켜져있는 경우일 때 뿐이므로 알림 저장
                msgDataUseCases.saveMsgDataUseCase(
                    DomainMsgData(
                        menuId = notiTypeId!!,
                        title = title,
                        subTitle = subTitle,
                        content = content,
                        timeStamp = timeStamp
                    )
                )
            }
        }

        if(currentGeneralSettings[TotalMenu.TTS_ENABLED.menuId]?.personalSetting?.isEnabled == true) {
            // TTS 읽기 기능이 켜져 있을 때에만 알림을 읽도록 처리
            notificationData =
                NotificationData(notiTypeId!!, title, subTitle, content, TTS_STATE.NONE)

            if (!::tts.isInitialized) {
                try {
                    tts = TextToSpeech(this, this)
                    speakNotification()
                } catch (e: Exception) {
                    logger.e("${this.javaClass.simpleName}, initTTS Error", "${e.message}")
                }
            } else {
                // 이미 초기화된 상태면 알림만 업데이트하고 다시 말하기
                notiBuilder?.changeNotificationMsg(this@MainService, TTS_STATE.NONE)
                speakNotification()
            }
        }

        return START_STICKY
    }

    private fun speakNotification() {
        if (notificationData == null || notiTypeId == null || isSpeaking) return

        TTSUtil.speakContent(tts, notiTypeId!!, notificationData!!.content)

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(p0: String?) {
                notiBuilder?.changeNotificationMsg(this@MainService, TTS_STATE.SPEAKING)
            }

            override fun onDone(p0: String?) {
                notiBuilder?.changeNotificationMsg(this@MainService, TTS_STATE.NONE)
            }

            override fun onError(p0: String?) {
                logger.e("${this.javaClass.simpleName}, TTS speaking Error", "$p0")
                notiBuilder?.changeNotificationMsg(this@MainService, TTS_STATE.ERROR)
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

    private fun observeAppSettings() {
        if (appSettingJob?.isActive == true) return

        appSettingJob = serviceScope.launch {
            SettingObserveManager.mergedAppSettingMap.collect { appSettingMap ->
                currentAppSettings = appSettingMap

                logger.d(
                    "${this@MainService::class.simpleName}",
                    "AppSettingMap updated: size=${appSettingMap.size}"
                )
            }
        }
    }

    private fun observeGeneralSettings() {
        if (generalSettingJob?.isActive == true) return

        generalSettingJob = serviceScope.launch {
            SettingObserveManager.mergedGeneralSettingMap.collect { generalSettingMap ->
                currentGeneralSettings = generalSettingMap

                logger.d(
                    "${this@MainService::class.simpleName}",
                    "generalSettingMap updated: size=${generalSettingMap.size}"
                )
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        serviceScope.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        super.onDestroy()
    }
}