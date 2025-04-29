package com.grusie.domain.repository

import com.grusie.domain.data.DomainTotalSettingDto

interface TotalSettingRepository {
    suspend fun initTotalSettingListUseCase()
    suspend fun saveLocalTotalSettingList(localTotalSettingList: List<DomainTotalSettingDto>)
    suspend fun getLocalTotalSettingList(): List<DomainTotalSettingDto>
    suspend fun deleteLocalTotalSettingList()
    suspend fun initPersonalSetting(uid: String?)
}