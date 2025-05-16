package com.grusie.domain.data

import com.grusie.core.common.SettingType

data class DomainTotalSettingDto(
    val menuId: Int = -1,
    val type: SettingType = SettingType.GENERAL,
    val isVisible: Boolean = false,
    val displayName: String = "",
    val isInitEnabled: Boolean = false,
    val description: String = "",
    val imageUrl: String? = null,
    val packageName: String? = null
)