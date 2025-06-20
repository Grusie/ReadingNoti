package com.grusie.core.appSetting

import kotlinx.serialization.Serializable

@Serializable
data class KakaoAppSettingData(
    val isQuietTtsEnabled: Boolean = true  // 알림을 꺼둔 채팅방의 TTS 여부
)