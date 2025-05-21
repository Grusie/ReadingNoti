package com.grusie.presentation.ui.base

abstract class BaseUiState {
    data object Idle : BaseUiState()
    data object Loading : BaseUiState()
}