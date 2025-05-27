package com.grusie.presentation.data.setting

import androidx.annotation.DrawableRes

abstract class BaseSettingMenu {
    // 화면에 표시될 아이콘
    @get:DrawableRes
    abstract val drawableResId: Int

    var radioButtonVisible: Boolean = true
        private set

    // 라디오 버튼의 보여짐 유무를 설정 할 때 사용
    open fun setRadioButtonVisible(isVisible: Boolean) {
        radioButtonVisible = isVisible
    }
}
