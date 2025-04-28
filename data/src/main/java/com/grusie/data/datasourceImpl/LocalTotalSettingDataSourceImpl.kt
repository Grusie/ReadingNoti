package com.grusie.data.datasourceImpl

import com.grusie.data.dao.LocalTotalSettingDao
import com.grusie.data.data.LocalTotalSettingEntity
import com.grusie.data.datasource.LocalTotalSettingDataSource
import javax.inject.Inject

class LocalTotalSettingDataSourceImpl @Inject constructor(
    private val localTotalSettingDao: LocalTotalSettingDao
) : LocalTotalSettingDataSource {
    override suspend fun getTotalSettingList(): List<LocalTotalSettingEntity> {
        return localTotalSettingDao.getLocalTotalSettingList()
    }

    override suspend fun saveTotalSettingList(localTotalSettingList: List<LocalTotalSettingEntity>) {
        localTotalSettingDao.insertLocalTotalSettingList(localTotalSettingList)
    }

    override suspend fun deleteTotalSettingList() {
        localTotalSettingDao.deleteLocalTotalSettingList()
    }
}