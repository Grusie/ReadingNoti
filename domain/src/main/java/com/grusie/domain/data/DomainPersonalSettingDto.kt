package com.grusie.domain.data

import com.grusie.core.common.SettingType

data class DomainPersonalSettingDto(
    val menuId: Int = -1,
    val type: SettingType = SettingType.GENERAL,
    val isEnabled: Boolean = false,
    val customData: String? = null
)