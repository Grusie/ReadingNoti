package com.grusie.presentation.data.setting.totalmenu

import com.grusie.presentation.data.setting.BaseSettingMenu

enum class TOTAL_APP_SETTING(val menuId: Int, val settingMenu: BaseSettingMenu) {
    TOTAL_NOTI_ENABLED(MenuId.TOTAL_NOTI_ENABLED, TotalNotiEnabled()),  // 알림
    FOCUS_MODE(MenuId.FOCUS_MODE, FocusMode()),    // 방해금지
    BOOT_ENABLED(MenuId.BOOT_ENABLED, BootEnabled()); // 부팅 시 자동 실행

    companion object {
        object MenuId {
            const val TOTAL_NOTI_ENABLED = 100
            const val FOCUS_MODE = 101
            const val BOOT_ENABLED = 102
        }

        fun getTotalAppSetting(menuId: Int): TOTAL_APP_SETTING? {
            var result: TOTAL_APP_SETTING? = null
            for(entry in TOTAL_APP_SETTING.entries){
                if(entry.menuId == menuId) {
                    result = entry
                    break
                }
            }

            return result
        }
    }
}