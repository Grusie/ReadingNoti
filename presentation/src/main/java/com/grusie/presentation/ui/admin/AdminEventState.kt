package com.grusie.presentation.ui.admin

import com.grusie.presentation.ui.base.BaseEventState

sealed class AdminEventState : BaseEventState() {
    data class Success(val data: Int) : AdminEventState()
    data class Error(val errorMsg: String) : AdminEventState()
    data class Toast(val toastMsg: String) : AdminEventState()
    data object Alert : AdminEventState()
    data class Confirm(val type: Int) : AdminEventState()
    data class Navigate(
        val route: String,
        val args: Map<String, String> = emptyMap(),
        val includeBackStack: Boolean = false
    ) :
        AdminEventState()
}