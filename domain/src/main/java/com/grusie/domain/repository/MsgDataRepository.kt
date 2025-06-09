package com.grusie.domain.repository

import com.grusie.domain.data.DomainMsgData
import kotlinx.coroutines.flow.Flow

interface MsgDataRepository {
    suspend fun saveMsgData(localMsgEntity: DomainMsgData)
    suspend fun deleteAllMsgData(menuId: Int)
    suspend fun observeMsgList(menuId: Int?): Flow<List<DomainMsgData>>
}