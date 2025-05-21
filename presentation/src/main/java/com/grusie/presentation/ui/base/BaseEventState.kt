package com.grusie.presentation.ui.base

abstract class BaseEventState {
    data class Error(val errorMsg: String) : BaseEventState()
    data class Navigate(
        val route: String,
        val includeBackStack: Boolean = false,
        val args: Map<String, String> = emptyMap()
    ) : BaseEventState()

    data class Alert(val title: String, val msg: String) : BaseEventState()
    data class Toast(val toastMsg: String) : BaseEventState()
}