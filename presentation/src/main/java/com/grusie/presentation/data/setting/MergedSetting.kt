package com.grusie.presentation.data.setting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto

data class MergedSetting(
    val totalSetting: UiTotalSettingDto,
    val personalSetting: DomainPersonalSettingDto?
)