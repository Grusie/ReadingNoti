package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import kotlinx.coroutines.flow.Flow

class ObserveLocalTotalSettingsUseCase(private val repository: TotalSettingRepository) {
    suspend operator fun invoke(): Flow<List<DomainTotalSettingDto>> {
        return repository.observeTotalSettings()
    }
}