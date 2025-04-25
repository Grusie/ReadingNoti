package com.grusie.data.datasource

import com.grusie.data.data.TotalSettingDto

interface TotalSettingDataSource {
    suspend fun getTotalSettingList(): Result<List<TotalSettingDto>>
}