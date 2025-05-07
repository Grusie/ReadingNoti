package com.grusie.presentation.ui.admin

import com.grusie.presentation.ui.base.BaseEventState

sealed class AdminEventState : BaseEventState() {
    data class Success(val data: Any?) : AdminEventState()
    data class Error(val errorMsg: String) : AdminEventState()
    data class Toast(val toastMsg: String) : AdminEventState()
    data class Navigate(
        val route: String,
        val args: Map<String, String> = emptyMap(),
        val includeBackStack: Boolean = false
    ) :
        AdminEventState()
}