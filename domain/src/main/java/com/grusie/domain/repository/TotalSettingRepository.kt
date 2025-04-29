package com.grusie.domain.repository

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto

interface TotalSettingRepository {
    suspend fun initTotalSettingListUseCase()
    suspend fun saveLocalTotalSettingList(localTotalSettingList: List<DomainTotalSettingDto>)
    suspend fun getLocalTotalSettingList(): List<DomainTotalSettingDto>
    suspend fun deleteLocalTotalSettingList()
    suspend fun initPersonalSetting(uid: String?)
    suspend fun getLocalPersonalSettingList(): List<DomainPersonalSettingDto>
    suspend fun changeSettingInfo(uid: String?, domainPersonalSettingDto: DomainPersonalSettingDto)
}