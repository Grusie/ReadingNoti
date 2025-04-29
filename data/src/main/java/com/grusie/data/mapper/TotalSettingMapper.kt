package com.grusie.data.mapper

import com.grusie.data.data.LocalPersonalSettingEntity
import com.grusie.data.data.LocalTotalSettingEntity
import com.grusie.data.data.PersonalSettingDto
import com.grusie.data.data.TotalSettingDto
import com.grusie.domain.data.DomainPersonalSettingDto
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

fun LocalTotalSettingEntity.toDomain(): DomainTotalSettingDto {
    return DomainTotalSettingDto(
        menuId = this.menuId,
        isVisible = this.isVisible,
        displayName = this.displayName,
        isInitEnabled = this.isInitEnabled,
        description = this.description
    )
}

fun DomainTotalSettingDto.toLocal(): LocalTotalSettingEntity {
    return LocalTotalSettingEntity(
        menuId = this.menuId,
        isVisible = this.isVisible,
        displayName = this.displayName,
        isInitEnabled = this.isInitEnabled,
        description = this.description
    )
}

fun LocalPersonalSettingEntity.toDomain(): DomainPersonalSettingDto {
    return DomainPersonalSettingDto(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        customData = this.customData
    )
}

fun PersonalSettingDto.toDomain(): DomainPersonalSettingDto {
    return DomainPersonalSettingDto(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        customData = this.customData
    )
}

fun PersonalSettingDto.toLocal(): LocalPersonalSettingEntity {
    return LocalPersonalSettingEntity(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        customData = this.customData
    )
}

fun DomainPersonalSettingDto.toLocal(): LocalPersonalSettingEntity {
    return LocalPersonalSettingEntity(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        customData = this.customData
    )
}