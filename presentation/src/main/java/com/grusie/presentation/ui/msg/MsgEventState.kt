package com.grusie.presentation.ui.msg

import com.grusie.presentation.ui.base.BaseEventState

sealed class MsgEventState: BaseEventState() {
    data class Success(val successType: Int) : MsgEventState()
    data class MsgConfirm(val type: Int) : MsgEventState()
}