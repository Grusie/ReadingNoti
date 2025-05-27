package com.grusie.domain.usecase.totalSetting

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import kotlinx.coroutines.flow.Flow

class ObserveLocalPersonalSettingsUseCase(private val repository: TotalSettingRepository) {
    suspend operator fun invoke(): Flow<List<DomainPersonalSettingDto>> {
        return repository.observePersonalSettings()
    }
}