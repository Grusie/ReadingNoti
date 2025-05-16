package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

class SaveLocalTotalSettingListUseCase @Inject constructor(
    private val repository: TotalSettingRepository
) {
    suspend operator fun invoke(domainTotalSettingList: List<DomainTotalSettingDto>) {
        repository.saveLocalTotalSettingList(domainTotalSettingList)
    }
}