package com.grusie.domain.repository

import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto

interface TotalSettingRepository {
    suspend fun initTotalSettingListUseCase()
    suspend fun getLocalTotalSettingList(): List<DomainTotalSettingDto>
    suspend fun initPersonalSetting(uid: String?)
    suspend fun getLocalPersonalSettingList(): List<DomainPersonalSettingDto>
    suspend fun changeSettingInfo(uid: String?, domainPersonalSettingDto: DomainPersonalSettingDto)
    suspend fun getPersonalSettingList(uid: String): Result<List<DomainPersonalSettingDto>>
    suspend fun setPersonalSettingList(
        uid: String,
        domainPersonalSettingList: List<DomainPersonalSettingDto>
    ): Result<Unit>

    suspend fun setLocalPersonalSettingList(domainPersonalSettingList: List<DomainPersonalSettingDto>)
}