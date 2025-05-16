package com.grusie.presentation.data.setting.totalmenu

import com.grusie.core.common.TotalMenu
import com.grusie.presentation.data.setting.BaseSettingMenu
import com.grusie.presentation.ui.setting.SettingViewModel

enum class TOTAL_APP_SETTING(val menuId: Int, val settingMenu: BaseSettingMenu) {
    TOTAL_NOTI_ENABLED(TotalMenu.TOTAL_NOTI_ENABLED.menuId, TotalNotiEnabled()),  // 알림
    FOCUS_MODE(TotalMenu.FOCUS_MODE.menuId, FocusMode()),    // 방해금지
    BOOT_ENABLED(TotalMenu.BOOT_ENABLED.menuId, BootEnabled()); // 부팅 시 자동 실행

    companion object {
        fun getTotalAppSetting(menuId: Int): TOTAL_APP_SETTING? {
            var result: TOTAL_APP_SETTING? = null
            for (entry in TOTAL_APP_SETTING.entries) {
                if (entry.menuId == menuId) {
                    result = entry
                    break
                }
            }

            return result
        }
    }

    suspend fun onClick(viewModel: SettingViewModel? = null) {
        settingMenu.onClickAction(this, viewModel)
    }

    suspend fun onRadioChanged(viewModel: SettingViewModel? = null, isSelected: Boolean) {
        settingMenu.onRadioChanged(this, viewModel, isSelected)
    }
}