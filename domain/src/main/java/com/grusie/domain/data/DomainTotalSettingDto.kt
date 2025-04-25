package com.grusie.domain.data

data class DomainTotalSettingDto(
    val menuId: Int = -1,
    val isVisible: Boolean = false,
    val displayName: String = "",
    val isInitEnabled: Boolean = false,
    val description: String = "",
)