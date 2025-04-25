package com.grusie.presentation.data.setting.detailmenu

import com.grusie.presentation.data.setting.BaseSettingMenu

enum class DETAIL_NOTI_SETTING(val baseSettingMenu: BaseSettingMenu) {
    ENABLED(NotiEnabled()),  // 알림
    FOCUSMODE(NotiEnabled())
}