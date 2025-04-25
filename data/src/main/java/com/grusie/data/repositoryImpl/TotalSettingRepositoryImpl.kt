package com.grusie.data.repositoryImpl

import com.grusie.data.datasource.TotalSettingDataSource
import com.grusie.data.mapper.toDomain
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

class TotalSettingRepositoryImpl @Inject constructor(
    private val totalSettingDataSource: TotalSettingDataSource
): TotalSettingRepository {
    override suspend fun getTotalSettingList(): Result<List<DomainTotalSettingDto>> {
        return totalSettingDataSource.getTotalSettingList().map {list ->
            list.map { it.toDomain() }
        }
    }
}