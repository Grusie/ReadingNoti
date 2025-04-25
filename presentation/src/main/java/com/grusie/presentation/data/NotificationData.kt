package com.grusie.presentation.data

/**
 * 알림이 가지고 있는 실제 데이터 + tts 상태 및 notiType
 */
data class NotificationData(
    val notiTypeId: Int,  // notiType의 id
    val title: String = "", // 제목
    val subTitle: String = "",  // 부제목
    val content: String = "",   // 내용
    val ttsState: TTS_STATE = TTS_STATE.NONE    // 현재 TTS의 상태
)