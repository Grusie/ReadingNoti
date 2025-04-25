package com.grusie.presentation.data.setting

import androidx.annotation.DrawableRes
import com.grusie.presentation.ui.setting.SettingViewModel

abstract class BaseSettingMenu {
    // 화면에 표시될 아이콘
    @get:DrawableRes
    abstract val drawableResId: Int

    var radioButtonVisible: Boolean = true
        private set

    // 설정 아이템을 클릭 했을 때의 동작 처리
    abstract fun onClickAction(viewModel: SettingViewModel? = null, selected: Boolean): Boolean

    /**
     * 라디오 버튼의 상태를 변경 시킬 때의 처리
     *
     * @return 서버통신 등 결과 상태를 리턴(radioSelected 설정 시 사용)
     */
    abstract fun onRadioChanged(viewModel: SettingViewModel? = null, selected: Boolean): Boolean

    // 라디오 버튼의 보여짐 유무를 설정 할 때 사용
    open fun setRadioButtonVisible(isVisible: Boolean) {
        radioButtonVisible = isVisible
    }
}
