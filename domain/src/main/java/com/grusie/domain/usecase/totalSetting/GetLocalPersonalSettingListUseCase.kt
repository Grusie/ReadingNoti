package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.repository.TotalSettingRepository

class GetLocalPersonalSettingListUseCase(
    private val repository: TotalSettingRepository
) {
    suspend operator fun invoke(): List<DomainPersonalSettingDto> {
        return repository.getLocalPersonalSettingList()
    }
}