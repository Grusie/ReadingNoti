package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.repository.TotalSettingRepository

/**
 * 로컬 DB에 있는 전체 설정 리스트를 삭제
 */
class DeleteLocalTotalSettingUseCase(
    private val totalSettingRepository: TotalSettingRepository
) {
    suspend operator fun invoke() {
        totalSettingRepository.deleteLocalTotalSettingList()
    }
}