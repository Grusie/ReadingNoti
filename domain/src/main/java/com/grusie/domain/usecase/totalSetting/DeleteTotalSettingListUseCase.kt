package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

/**
 * 전체 설정을 삭제 할 수 있음(APP 타입만 사용)
 * 다중 처리를 위해 list로 받도록 처리
 */
class DeleteTotalSettingListUseCase @Inject constructor(
    private val repository: TotalSettingRepository
) {
    suspend operator fun invoke(domainTotalSettingDocNameList: List<String>): Result<Unit> {
        return repository.deleteTotalSettingList(domainTotalSettingDocNameList)
    }
}