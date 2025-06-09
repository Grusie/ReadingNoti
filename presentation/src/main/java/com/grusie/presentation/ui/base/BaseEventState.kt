package com.grusie.presentation.ui.base

abstract class BaseEventState {
    data class Error(val errorMsg: String) : BaseEventState()
    data class Navigate(
        val route: String,
        val includeBackStack: Boolean = false,
        val args: Map<String, Any> = emptyMap()
    ) : BaseEventState()
    data object PopBackStack : BaseEventState()

    data class Alert(val title: String, val msg: String, val onConfirm: () -> Unit = {}) : BaseEventState()
    data class Confirm(val title: String, val msg: String, val confirmType: Int) : BaseEventState()
    data class Toast(val toastMsg: String) : BaseEventState()
    data class BroadCast(val broadCastAction: String) : BaseEventState()
}