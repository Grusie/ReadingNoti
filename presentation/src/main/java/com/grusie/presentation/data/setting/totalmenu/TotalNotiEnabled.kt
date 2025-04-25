package com.grusie.presentation.data.setting.totalmenu

import com.grusie.presentation.R
import com.grusie.presentation.data.setting.BaseSettingMenu
import com.grusie.presentation.ui.setting.SettingViewModel

/**
 * 전체 알림을 여부를 설정
 */
class TotalNotiEnabled: BaseSettingMenu() {
    override val drawableResId: Int
        get() = R.drawable.ic_noti_black

    override fun onClickAction(viewModel: SettingViewModel?, selected: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun onRadioChanged(viewModel: SettingViewModel?, selected: Boolean): Boolean {
        TODO("Not yet implemented")
    }

}