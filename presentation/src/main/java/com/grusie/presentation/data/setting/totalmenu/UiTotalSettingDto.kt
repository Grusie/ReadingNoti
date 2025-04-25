package com.grusie.presentation.data.setting.totalmenu

data class UiTotalSettingDto(
    val menuId: Int = -1,
    val isVisible: Boolean = false,
    val displayName: String = "",
    val isInitEnabled: Boolean = false,
    val description: String = "",
    val totalAppSettingEnum: TOTAL_APP_SETTING? = null
)