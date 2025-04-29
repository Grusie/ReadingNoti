package com.grusie.domain.data

data class DomainPersonalSettingDto(
    val menuId: Int = -1,
    val isEnabled: Boolean = false,
    val customData: String? = null
)