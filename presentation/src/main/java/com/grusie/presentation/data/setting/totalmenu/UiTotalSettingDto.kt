package com.grusie.presentation.data.setting.totalmenu

import com.grusie.core.common.SettingType
import kotlinx.serialization.Serializable

@Serializable
data class UiTotalSettingDto(
    val menuId: Int = -1,
    val type: SettingType = SettingType.GENERAL,
    val isVisible: Boolean = false,
    val displayName: String = "",
    val isInitEnabled: Boolean = false,
    val description: String = "",
    val totalAppSettingEnum: TOTAL_APP_SETTING? = null,
    val imageUrl: String? = null,
    val packageName: String? = null
)