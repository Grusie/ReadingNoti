package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.repository.TotalSettingRepository

class ChangeSettingInfoUseCase(private val repository: TotalSettingRepository) {
    suspend operator fun invoke(
        uid: String? = null,
        domainPersonalSettingDto: DomainPersonalSettingDto
    ) {
        repository.changeSettingInfo(uid = uid, domainPersonalSettingDto = domainPersonalSettingDto)
    }
}