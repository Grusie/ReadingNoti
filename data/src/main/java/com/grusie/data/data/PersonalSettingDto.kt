package com.grusie.data.data

import com.grusie.core.common.SettingType

data class PersonalSettingDto(
    val menuId: Int = -1,
    val type: SettingType = SettingType.GENERAL,
    val isEnabled: Boolean = false,
    val customData: String? = null
)