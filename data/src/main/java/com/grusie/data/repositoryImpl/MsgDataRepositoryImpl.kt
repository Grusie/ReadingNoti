package com.grusie.data.repositoryImpl

import com.grusie.data.datasource.LocalMsgDataSource
import com.grusie.data.mapper.toDomain
import com.grusie.data.mapper.toEntity
import com.grusie.domain.data.DomainMsgData
import com.grusie.domain.repository.MsgDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MsgDataRepositoryImpl @Inject constructor(
    private val dataSource: LocalMsgDataSource
): MsgDataRepository {
    override suspend fun saveMsgData(localMsgEntity: DomainMsgData) {
        dataSource.saveLocalMsgData(localMsgEntity.toEntity())
    }

    override suspend fun deleteAllMsgData(menuId: Int) {
        dataSource.deleteAllLocalMsgData(menuId)
    }

    override suspend fun observeMsgList(menuId: Int?): Flow<List<DomainMsgData>> {
        return dataSource.observeMsgList(menuId).map { list -> list.map { it.toDomain() } }
    }
}