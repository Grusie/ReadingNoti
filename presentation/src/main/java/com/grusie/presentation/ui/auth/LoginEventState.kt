package com.grusie.presentation.ui.auth

import com.grusie.presentation.ui.base.BaseEventState

sealed class LoginEventState: BaseEventState() {
    data class Error(val errorMsg: String): LoginEventState()
    data class Navigate(val route: String, val includeBackStack: Boolean = false): LoginEventState()
}