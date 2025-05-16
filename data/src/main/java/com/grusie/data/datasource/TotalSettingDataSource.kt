package com.grusie.data.datasource

import com.grusie.core.common.SettingType
import com.grusie.data.data.PersonalSettingDto
import com.grusie.data.data.TotalSettingDto
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto

interface TotalSettingDataSource {
    suspend fun getTotalSettingList(type: SettingType? = null): Result<List<TotalSettingDto>>
    suspend fun getPersonalSettingList(uid: String): Result<List<PersonalSettingDto>>
    suspend fun setPersonalSettingList(
        uid: String,
        list: List<DomainPersonalSettingDto>
    ): Result<Unit>

    suspend fun setTotalSetting(
        initTotalSettingDto: DomainTotalSettingDto?,
        domainTotalSettingDto: DomainTotalSettingDto
    ): Result<Unit>

    suspend fun updateTotalSetting(menuId: Int, field: Map<String, Any>): Result<Unit>
}