package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.repository.TotalSettingRepository

class SetLocalPersonalSettingListUseCase(private val repository: TotalSettingRepository) {
    suspend operator fun invoke(domainPersonalSettingList: List<DomainPersonalSettingDto>) {
        repository.setLocalPersonalSettingList(domainPersonalSettingList)
    }
}