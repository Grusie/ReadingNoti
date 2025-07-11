package com.grusie.data.mapper

import com.grusie.core.appSetting.AppPackageEnum
import com.grusie.core.appSetting.AppPackageEnum.Companion.from
import com.grusie.core.appSetting.AppPackageEnum.KAKAO
import com.grusie.core.appSetting.BaseAppSetting
import com.grusie.core.appSetting.KakaoAppSetting
import com.grusie.core.appSetting.KakaoAppSettingData
import com.grusie.core.utils.LoggerProvider
import com.grusie.data.data.LocalPersonalSettingEntity
import com.grusie.data.data.LocalTotalSettingEntity
import com.grusie.data.data.PersonalSettingDto
import com.grusie.data.data.TotalSettingDto
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto
import kotlinx.serialization.json.Json

fun TotalSettingDto.toDomain(): DomainTotalSettingDto {
    return DomainTotalSettingDto(
        menuId = this.menuId,
        type = this.type,
        isVisible = this.isVisible,
        displayName = this.displayName,
        isInitEnabled = this.isInitEnabled,
        description = this.description,
        imageUrl = this.imageUrl,
        packageName = this.packageName,
        docName = this.docName,
        isTintUse = this.isTintUse
    )
}

fun LocalTotalSettingEntity.toDomain(): DomainTotalSettingDto {
    return DomainTotalSettingDto(
        menuId = this.menuId,
        type = this.type,
        isVisible = this.isVisible,
        displayName = this.displayName,
        isInitEnabled = this.isInitEnabled,
        description = this.description,
        imageUrl = this.imageUrl,
        packageName = this.packageName,
        docName = this.docName,
        isTintUse = this.isTintUse
    )
}

fun DomainTotalSettingDto.toLocal(): LocalTotalSettingEntity {
    return LocalTotalSettingEntity(
        menuId = this.menuId,
        type = this.type,
        isVisible = this.isVisible,
        displayName = this.displayName,
        isInitEnabled = this.isInitEnabled,
        description = this.description,
        imageUrl = this.imageUrl,
        packageName = this.packageName,
        docName = this.docName,
        isTintUse = this.isTintUse
    )
}

fun LocalPersonalSettingEntity.toDomain(): DomainPersonalSettingDto {
    return DomainPersonalSettingDto(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        customData = jsonCustomData?.let { jsonCustomData ->
            packageName?.let { packageName ->
                getAppCustomData(packageName, jsonCustomData)
            }
        },
        packageName = this.packageName
    )
}

fun PersonalSettingDto.toDomain(): DomainPersonalSettingDto {
    return DomainPersonalSettingDto(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        customData =
        this.jsonCustomData?.let { jsonCustomData ->
            this.packageName?.let { packageName ->
                getAppCustomData(packageName, jsonCustomData)
            }
        },
        packageName = this.packageName
    )
}

fun PersonalSettingDto.toLocal(): LocalPersonalSettingEntity {
    return LocalPersonalSettingEntity(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        jsonCustomData = this.jsonCustomData,
        packageName = this.packageName
    )
}

fun DomainPersonalSettingDto.toLocal(): LocalPersonalSettingEntity {
    return LocalPersonalSettingEntity(
        menuId = this.menuId,
        isEnabled = this.isEnabled,
        jsonCustomData = this.customData?.let { customData ->
            this.packageName?.let {packageName ->
                getAppCustomDataJson(packageName, customData)
            }
        },
        packageName = this.packageName
    )
}

fun getAppCustomData(packageName: String, jsonCustomData: String): BaseAppSetting? {
    return when (AppPackageEnum.from(packageName)) {
        KAKAO -> {
            try {
                KakaoAppSetting(Json.decodeFromString<KakaoAppSettingData>(jsonCustomData))
            } catch (e: Exception) {
                LoggerProvider.logger.e("PersonalSettingDto to Domain Error", "${e.message}")
                null
            }
        }

        else -> null
    }
}


fun getAppCustomDataJson(packageName: String, baseAppSetting: BaseAppSetting?): String? {
    val json = Json {
        encodeDefaults = true
    }

    var parsingData = baseAppSetting?.parsingData

    when (from(packageName)) {
        KAKAO -> {
            if (parsingData == null) {
                parsingData = KakaoAppSettingData()
            }
            return try {
                json.encodeToString(parsingData as KakaoAppSettingData)
            } catch (e: Exception) {
                LoggerProvider.logger.e(
                    "AppPackageEnum getAppSettingJson Error",
                    "${e.message}"
                )
                null
            }
        }

        else -> {
            return null
        }
    }
}