package com.grusie.data.mapper

import com.grusie.data.data.TotalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto

fun TotalSettingDto.toDomain(): DomainTotalSettingDto {
    return DomainTotalSettingDto(
        menuId = this.menuId,
        isVisible = this.isVisible,
        displayName = this.displayName,
        isInitEnabled = this.isInitEnabled,
        description = this.description
    )
}