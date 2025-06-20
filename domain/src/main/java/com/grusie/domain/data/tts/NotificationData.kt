package com.grusie.domain.data.tts

import java.io.Serializable

/**
 * 알림이 가지고 있는 실제 데이터 + tts 상태 및 notiType
 */
data class NotificationData(
    val notiMenuId: Int,  // notiType의 id
    val packageName: String = "",   // 패키지명
    val importance: Int = 0,    // 알림 중요도
    val channel: String = "",   // 채널명
    val title: String = "", // 제목
    val subTitle: String = "",  // 부제목
    val content: String = "",   // 내용
    val timeStamp: Long = 0,    // 알림 시간
    val ttsState: TTS_STATE = TTS_STATE.NONE    // 현재 TTS의 상태
) : Serializable