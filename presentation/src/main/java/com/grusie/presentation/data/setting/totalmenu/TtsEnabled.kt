package com.grusie.presentation.data.setting.totalmenu

import com.grusie.presentation.R
import com.grusie.presentation.data.setting.BaseSettingMenu

/**
 * TTS 여부를 설정
 */
class TtsEnabled : BaseSettingMenu() {
    override val drawableResId: Int
        get() = R.drawable.ic_speaker
}