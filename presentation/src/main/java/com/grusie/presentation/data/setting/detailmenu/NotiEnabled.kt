package com.grusie.presentation.data.setting.detailmenu

import com.grusie.presentation.data.setting.BaseSettingMenu
import com.grusie.presentation.ui.setting.SettingViewModel

/**
 * 해당 앱의 알림 상태를 변경
 */
class NotiEnabled : BaseSettingMenu() {
    override val drawableResId: Int
        get() = TODO("Not yet implemented")

    override fun onClickAction(viewModel: SettingViewModel?, selected: Boolean): Boolean {
        // 방해금지 알림 시간을 변경하는 다이얼로그를 띄우는 동작을 위해 서버통신X
        return false
    }

    override fun onRadioChanged(viewModel: SettingViewModel?, selected: Boolean): Boolean {
        TODO("Not yet implemented")
    }
}