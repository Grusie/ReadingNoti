package com.grusie.presentation.ui.splash

import com.grusie.presentation.ui.base.BaseUiState

sealed class SplashUiState:BaseUiState() {
    data object Idle: SplashUiState()
    data object Loading: SplashUiState()
}