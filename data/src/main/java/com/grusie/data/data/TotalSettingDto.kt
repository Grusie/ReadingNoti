package com.grusie.data.data

import com.grusie.core.common.SettingType

data class TotalSettingDto(
    var menuId: Int = -1,
    val type: SettingType = SettingType.GENERAL,
    var isVisible: Boolean = false,
    var displayName: String = "",
    var isInitEnabled: Boolean = false,
    var description: String = "",
    var imageUrl: String? = null
)