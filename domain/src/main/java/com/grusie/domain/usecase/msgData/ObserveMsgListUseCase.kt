package com.grusie.domain.usecase.msgData

import com.grusie.domain.data.DomainMsgData
import com.grusie.domain.repository.MsgDataRepository
import kotlinx.coroutines.flow.Flow

class ObserveMsgListUseCase(private val repository: MsgDataRepository) {
    suspend operator fun invoke(menuId: Int? = null): Flow<List<DomainMsgData>> {
        return repository.observeMsgList(menuId)
    }
}