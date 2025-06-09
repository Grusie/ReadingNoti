package com.grusie.domain.usecase.msgData

import com.grusie.domain.data.DomainMsgData
import com.grusie.domain.repository.MsgDataRepository

class SaveMsgDataUseCase(private val repository: MsgDataRepository) {
    suspend operator fun invoke(domainMsgData: DomainMsgData) {
        repository.saveMsgData(domainMsgData)
    }
}