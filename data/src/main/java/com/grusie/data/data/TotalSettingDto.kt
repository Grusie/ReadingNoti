package com.grusie.data.data

import androidx.annotation.Keep

@Keep
data class TotalSettingDto(
    var menuId: Int = -1,
    var isVisible: Boolean = false,
    var displayName: String = "",
    var isInitEnabled: Boolean = false,
    var description: String = "",
)