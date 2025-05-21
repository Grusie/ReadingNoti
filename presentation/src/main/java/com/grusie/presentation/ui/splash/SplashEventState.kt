package com.grusie.presentation.ui.splash

import com.grusie.presentation.ui.base.BaseEventState

sealed class SplashEventState : BaseEventState() {
    data class FinishableError(val errorMsg: String, val isFinishApp: Boolean = false) :
        SplashEventState()
}