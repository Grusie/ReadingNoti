package com.grusie.domain.usecase.msgData

import com.grusie.domain.repository.MsgDataRepository

class DeleteAllMsgDataUseCase(private val repository: MsgDataRepository) {
    suspend operator fun invoke(menuId: Int) {
        repository.deleteAllMsgData(menuId)
    }
}