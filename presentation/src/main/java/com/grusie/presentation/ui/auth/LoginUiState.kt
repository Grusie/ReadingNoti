package com.grusie.presentation.ui.auth

import com.grusie.presentation.ui.base.BaseUiState

sealed class LoginUiState : BaseUiState() {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
}