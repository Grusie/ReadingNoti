package com.grusie.presentation.ui.main

import com.grusie.presentation.ui.base.BaseUiState

sealed class MainUiState:BaseUiState() {
    data object Idle: MainUiState()
    data object Loading: MainUiState()
}