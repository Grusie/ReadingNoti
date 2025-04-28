package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

/**
 * 전체 설정 리스트 초기화
 * 화면에 보여줄 설정 리스트들의 기본 정보들을 서버에서 불러와 로컬 DB에 저장
 */
class InitTotalSettingListUseCase @Inject constructor(
    private val totalSettingRepository: TotalSettingRepository
) {
    suspend operator fun invoke() {
        return totalSettingRepository.initTotalSettingListUseCase()
    }
}