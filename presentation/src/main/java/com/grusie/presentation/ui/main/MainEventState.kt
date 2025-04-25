package com.grusie.presentation.ui.main

import com.grusie.presentation.ui.base.BaseEventState

sealed class MainEventState: BaseEventState() {
    data class Success(val data: Any?) : MainEventState()
    data class Error(val errorMsg: String): MainEventState()
    data class Navigate(val route: String, val includeBackStack: Boolean = false): MainEventState()
}