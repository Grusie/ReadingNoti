package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.repository.TotalSettingRepository

class SetPersonalSettingListUseCase(private val repository: TotalSettingRepository) {
    suspend operator fun invoke(
        uid: String,
        domainPersonalSettingList: List<DomainPersonalSettingDto>
    ): Result<Unit> {
        return repository.setPersonalSettingList(uid, domainPersonalSettingList)
    }
}