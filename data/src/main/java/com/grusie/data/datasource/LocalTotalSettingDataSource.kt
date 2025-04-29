package com.grusie.data.datasource

import com.grusie.data.data.LocalPersonalSettingEntity
import com.grusie.data.data.LocalTotalSettingEntity

interface LocalTotalSettingDataSource {
    suspend fun getTotalSettingList(): List<LocalTotalSettingEntity>
    suspend fun saveTotalSettingList(localTotalSettingList: List<LocalTotalSettingEntity>)
    suspend fun deleteTotalSettingList()
    suspend fun getPersonalSettingList(): List<LocalPersonalSettingEntity>
    suspend fun savePersonalSettingList(localPersonalSettingList: List<LocalPersonalSettingEntity>)
    suspend fun changePersonalSetting(localPersonalSettingEntity: LocalPersonalSettingEntity)
    suspend fun deletePersonalSettingList()
}