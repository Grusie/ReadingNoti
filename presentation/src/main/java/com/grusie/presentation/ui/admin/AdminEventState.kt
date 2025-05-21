package com.grusie.presentation.ui.admin

import com.grusie.presentation.ui.base.BaseEventState

sealed class AdminEventState : BaseEventState() {
    data class Success(val successType: Int) : AdminEventState()
    data class Confirm(val type: Int) : AdminEventState()
}