package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

/**
 * 전체 설정 기본값 설정
 * 관리자페이지에서 사용하며 사용자들의 전체 설정에 대한 기본값을 관리
 */
class SetTotalSettingUseCase @Inject constructor(
    private val totalSettingRepository: TotalSettingRepository
) {
    suspend operator fun invoke(
        initTotalSettingDto: DomainTotalSettingDto? = null,
        domainTotalSettingDto: DomainTotalSettingDto
    ): Result<Unit> {
        return totalSettingRepository.setTotalSetting(initTotalSettingDto, domainTotalSettingDto)
    }
}