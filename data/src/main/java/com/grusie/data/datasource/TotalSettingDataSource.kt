package com.grusie.data.datasource

import com.grusie.data.data.PersonalSettingDto
import com.grusie.data.data.TotalSettingDto
import com.grusie.domain.data.DomainPersonalSettingDto

interface TotalSettingDataSource {
    suspend fun getTotalSettingList(): Result<List<TotalSettingDto>>
    suspend fun getPersonalSettingList(uid: String): Result<List<PersonalSettingDto>>
    suspend fun setPersonalSettingList(
        uid: String,
        list: List<DomainPersonalSettingDto>
    ): Result<Unit>
}