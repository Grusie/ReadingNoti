package com.grusie.core.appSetting

/**
 * 카카오에만 해당하는 설정
 */
class KakaoAppSetting(
    override val parsingData: KakaoAppSettingData
) : BaseAppSetting() {
    val isQuietTtsEnabled: Boolean get() = parsingData.isQuietTtsEnabled

    fun updateQuietTtsEnabled(newValue: Boolean): KakaoAppSetting {
        return KakaoAppSetting(parsingData.copy(isQuietTtsEnabled = newValue))
    }

    enum class KakaoAppSettingField(
        override val type: Int,
        override val title: String,
        override val description: String,
    ) : AppSettingFieldModel {
        QuiteTtsEnabled(
            type = AppSettingFieldType.BOOLEAN_TYPE,
            title = "알림을 꺼둔 대화방 TTS 설정",
            description = "알림을 꺼둔 대화방의 TTS를 설정 할 수 있습니다."
        )
    }

    companion object {
        const val QUIET_MSG_CHANNEL = "quiet_new_message"   // 알림을 꺼둔 채팅방의 알림 channel
    }
}