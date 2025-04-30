package com.grusie.presentation.ui.admin

import com.grusie.presentation.ui.base.BaseUiState

sealed class AdminUiState : BaseUiState() {
    data object Idle : AdminUiState()
    data object Loading : AdminUiState()
}