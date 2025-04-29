package com.grusie.data.data

data class PersonalSettingDto(
    val menuId: Int = -1,
    val isEnabled: Boolean = false,
    val customData: String? = null
)