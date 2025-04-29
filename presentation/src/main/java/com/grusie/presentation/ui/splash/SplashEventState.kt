package com.grusie.presentation.ui.splash

import com.grusie.presentation.ui.base.BaseEventState

sealed class SplashEventState : BaseEventState() {
    data class Success(val data: Any?) : SplashEventState()
    data class Error(val errorMsg: String, val isFinishApp: Boolean = false) : SplashEventState()
    data class Navigate(val route: String, val includeBackStack: Boolean = false) :
        SplashEventState()
}