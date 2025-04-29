package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.repository.TotalSettingRepository

class GetPersonalSettingUseCase(private val repository: TotalSettingRepository) {
    suspend operator fun invoke(uid: String): Result<List<DomainPersonalSettingDto>> {
        return repository.getPersonalSettingList(uid)
    }
}