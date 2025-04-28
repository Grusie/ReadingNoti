package com.grusie.data.datasource

import com.grusie.data.data.LocalTotalSettingEntity

interface LocalTotalSettingDataSource {
    suspend fun getTotalSettingList(): List<LocalTotalSettingEntity>
    suspend fun saveTotalSettingList(localTotalSettingList: List<LocalTotalSettingEntity>)
    suspend fun deleteTotalSettingList()
}