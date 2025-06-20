package com.grusie.domain.data

import com.grusie.core.appSetting.BaseAppSetting

data class DomainPersonalSettingDto(
    val menuId: Int = -1,
    val isEnabled: Boolean = false,
    val customData: BaseAppSetting? = null,
    val packageName: String? = null
)