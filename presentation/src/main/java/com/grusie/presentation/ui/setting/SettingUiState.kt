package com.grusie.presentation.ui.setting

import com.grusie.presentation.ui.base.BaseUiState

sealed class SettingUiState : BaseUiState() {
    data object Idle : SettingUiState()
    data object Loading : SettingUiState()
}