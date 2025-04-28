package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository

/**
 * 전체 설정 리스트 불러오기
 * 로컬 DB에 있는 전체 설정 리스트를 불러온다.
 */
class GetLocalTotalSettingListUseCase(
    private val totalSettingRepository: TotalSettingRepository
) {
    suspend operator fun invoke(): List<DomainTotalSettingDto> {
        return totalSettingRepository.getLocalTotalSettingList()
    }
}