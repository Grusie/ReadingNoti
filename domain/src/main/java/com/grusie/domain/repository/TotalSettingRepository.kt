package com.grusie.domain.repository

import com.grusie.domain.data.DomainTotalSettingDto

interface TotalSettingRepository {
    suspend fun getTotalSettingList(): Result<List<DomainTotalSettingDto>>
}