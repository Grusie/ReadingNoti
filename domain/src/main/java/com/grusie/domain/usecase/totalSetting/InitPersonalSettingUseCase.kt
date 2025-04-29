package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.repository.TotalSettingRepository

class InitPersonalSettingUseCase(private val repository: TotalSettingRepository) {
    suspend operator fun invoke(uid: String? = null) {
        repository.initPersonalSetting(uid)
    }
}