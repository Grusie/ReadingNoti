package com.grusie.data.datasourceImpl

import com.grusie.data.dao.LocalPersonalSettingDao
import com.grusie.data.dao.LocalTotalSettingDao
import com.grusie.data.data.LocalPersonalSettingEntity
import com.grusie.data.data.LocalTotalSettingEntity
import com.grusie.data.datasource.LocalTotalSettingDataSource
import javax.inject.Inject

class LocalTotalSettingDataSourceImpl @Inject constructor(
    private val localTotalSettingDao: LocalTotalSettingDao,
    private val localPersonalSettingDao: LocalPersonalSettingDao
) : LocalTotalSettingDataSource {
    override suspend fun getTotalSettingList(): List<LocalTotalSettingEntity> {
        return localTotalSettingDao.getLocalTotalSettingList()
    }

    override suspend fun saveTotalSettingList(localTotalSettingList: List<LocalTotalSettingEntity>) {
        localTotalSettingDao.deleteLocalTotalSettingList()
        localTotalSettingDao.insertLocalTotalSettingList(localTotalSettingList)
    }

    override suspend fun deleteTotalSettingList() {
        localTotalSettingDao.deleteLocalTotalSettingList()
    }

    override suspend fun getPersonalSettingList(): List<LocalPersonalSettingEntity> {
        return localPersonalSettingDao.getLocalPersonalSettingList()
    }

    override suspend fun savePersonalSettingList(localPersonalSettingList: List<LocalPersonalSettingEntity>) {
        localPersonalSettingDao.deleteLocalPersonalSettingList()
        localPersonalSettingDao.insertLocalPersonalSettingList(localPersonalSettingList)
    }

    override suspend fun changePersonalSetting(localPersonalSettingEntity: LocalPersonalSettingEntity) {
        localPersonalSettingDao.insertLocalPersonalSetting(localPersonalSettingEntity)
    }

    override suspend fun deletePersonalSettingList() {
        localPersonalSettingDao.deleteLocalPersonalSettingList()
    }
}