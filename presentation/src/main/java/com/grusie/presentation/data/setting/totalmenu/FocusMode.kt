package com.grusie.presentation.data.setting.totalmenu

import com.grusie.presentation.R
import com.grusie.presentation.data.setting.BaseSettingMenu
import com.grusie.presentation.ui.setting.SettingViewModel

/**
 * 방해금지 알림 시간을 설정
 */
class FocusMode: BaseSettingMenu() {
    override val drawableResId: Int
        get() = R.drawable.ic_focus_mode_black

    override fun onClickAction(viewModel: SettingViewModel?, selected: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun onRadioChanged(viewModel: SettingViewModel?, selected: Boolean): Boolean {
        TODO("Not yet implemented")
    }

}