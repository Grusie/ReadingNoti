package com.grusie.presentation.mapper

import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto

fun DomainTotalSettingDto.toUi(): UiTotalSettingDto {
    return UiTotalSettingDto(
        menuId = this.menuId,
        isVisible = this.isVisible,
        displayName = this.displayName,
        isInitEnabled = this.isInitEnabled,
        description = this.description,
        totalAppSettingEnum = TOTAL_APP_SETTING.getTotalAppSetting(this.menuId)
    )
}