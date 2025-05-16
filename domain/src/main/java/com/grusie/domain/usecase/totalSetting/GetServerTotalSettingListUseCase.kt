package com.grusie.domain.usecase.totalSetting

import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

/**
 * 서버에 있는 전체 설정 리스트를 로드
 */
class GetServerTotalSettingListUseCase @Inject constructor(
    private val totalSettingRepository: TotalSettingRepository
) {
    suspend operator fun invoke(type: SettingType? = null): Result<List<DomainTotalSettingDto>> {
        return totalSettingRepository.getServerTotalSettingList(type)
    }
}