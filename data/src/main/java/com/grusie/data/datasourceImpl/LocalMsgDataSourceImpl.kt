package com.grusie.data.datasourceImpl

import com.grusie.data.dao.LocalMsgDao
import com.grusie.data.data.LocalMsgEntity
import com.grusie.data.datasource.LocalMsgDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalMsgDataSourceImpl @Inject constructor(
    private val localMsgDao: LocalMsgDao
): LocalMsgDataSource {
    override suspend fun saveLocalMsgData(localMsgEntity: LocalMsgEntity) {
        localMsgDao.insertMessage(localMsgEntity)
        localMsgDao.trimOldMessages(localMsgEntity.menuId)
    }

    override suspend fun deleteAllLocalMsgData(menuId: Int) {
        localMsgDao.deleteMessages(menuId)
    }

    override suspend fun observeMsgList(menuId: Int?): Flow<List<LocalMsgEntity>> {
        return menuId?.let { localMsgDao.getRecentMessages(menuId) } ?: localMsgDao.getRecentMessagesAll()
    }
}