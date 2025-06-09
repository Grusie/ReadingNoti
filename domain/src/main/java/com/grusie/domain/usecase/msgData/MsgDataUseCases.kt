package com.grusie.domain.usecase.msgData

data class MsgDataUseCases(
    val saveMsgDataUseCase: SaveMsgDataUseCase,
    val deleteAllMsgDataUseCase: DeleteAllMsgDataUseCase,
    val observeMsgListUseCase: ObserveMsgListUseCase
)