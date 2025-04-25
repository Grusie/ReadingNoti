package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

class GetTotalSettingListUseCase @Inject constructor(
    private val totalSettingRepository: TotalSettingRepository
) {
    suspend operator fun invoke(): Result<List<DomainTotalSettingDto>> {
        return totalSettingRepository.getTotalSettingList()
    }
}