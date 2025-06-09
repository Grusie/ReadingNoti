package com.grusie.data.datasource

import com.grusie.data.data.LocalMsgEntity
import kotlinx.coroutines.flow.Flow

interface LocalMsgDataSource {
    suspend fun saveLocalMsgData(localMsgEntity: LocalMsgEntity)
    suspend fun deleteAllLocalMsgData(menuId: Int)
    suspend fun observeMsgList(menuId: Int?): Flow<List<LocalMsgEntity>>
}