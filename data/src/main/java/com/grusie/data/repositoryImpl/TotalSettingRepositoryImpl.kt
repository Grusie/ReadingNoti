package com.grusie.data.repositoryImpl

import com.grusie.core.utils.Logger
import com.grusie.data.data.DefaultValues
import com.grusie.data.datasource.LocalTotalSettingDataSource
import com.grusie.data.datasource.TotalSettingDataSource
import com.grusie.data.mapper.toDomain
import com.grusie.data.mapper.toLocal
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import javax.inject.Inject

class TotalSettingRepositoryImpl @Inject constructor(
    private val totalSettingDataSource: TotalSettingDataSource,
    private val localTotalSettingDataSource: LocalTotalSettingDataSource
) : TotalSettingRepository {
    override suspend fun initTotalSettingListUseCase() {
        try {
            val result = totalSettingDataSource.getTotalSettingList()

            result.map { list ->
                // 서버에서 가져온 데이터를 Room DB에 저장
                saveLocalTotalSettingList(list.map { it.toDomain() })
                list.map { it.toDomain() }
            }.getOrElse { e ->
                // 서버 통신 실패 시 예외를 던짐
                throw e
            }
        } catch (e: Exception) {
            Logger.log(Logger.LogType.LOG_TYPE_E, this@TotalSettingRepositoryImpl::class.java.simpleName, "${e.message}")
            // 로컬DB에 전체 설정에 관한 정보가 있으면 냅두고, 그렇지 않다면 기본 값을 설정
            val localData = localTotalSettingDataSource.getTotalSettingList()
            if (localData.isEmpty()) {
                saveLocalTotalSettingList(DefaultValues.initLocalTotalSettingList.map { it.toDomain() })
            }
        }
    }

    override suspend fun getLocalTotalSettingList(): List<DomainTotalSettingDto> {
        return localTotalSettingDataSource.getTotalSettingList().map { it.toDomain() }
    }

    override suspend fun saveLocalTotalSettingList(localTotalSettingList: List<DomainTotalSettingDto>) {
        localTotalSettingDataSource.saveTotalSettingList(localTotalSettingList.map { it.toLocal() })
    }

    override suspend fun deleteLocalTotalSettingList() {
        localTotalSettingDataSource.deleteTotalSettingList()
    }
}