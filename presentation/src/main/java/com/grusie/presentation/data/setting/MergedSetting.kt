package com.grusie.presentation.data.setting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto

data class MergedSetting(
    val totalSetting: DomainTotalSettingDto,
    val personalSetting: DomainPersonalSettingDto?
)