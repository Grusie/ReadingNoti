package com.grusie.domain.repository

import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto

interface TotalSettingRepository {
    suspend fun getServerTotalSettingList(type: SettingType?): Result<List<DomainTotalSettingDto>>
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
    suspend fun updateTotalSettingVisibility(menuId: Int, isVisible: Boolean): Result<Unit>
    suspend fun setTotalSetting(
        initTotalSettingDto: DomainTotalSettingDto?,
        domainTotalSettingDto: DomainTotalSettingDto
    ): Result<Unit>
    suspend fun saveLocalTotalSettingList(
        domainTotalSettingList: List<DomainTotalSettingDto>
    )
    suspend fun deleteTotalSettingList(
        domainTotalSettingDocNameList: List<String>
    ): Result<Unit>
}