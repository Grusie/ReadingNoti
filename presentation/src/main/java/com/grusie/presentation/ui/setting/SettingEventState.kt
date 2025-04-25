package com.grusie.presentation.ui.setting

import com.grusie.presentation.ui.base.BaseEventState

sealed class SettingEventState: BaseEventState() {
    data class Success(val data: Any?) : SettingEventState()
    data class Error(val errorMsg: String): SettingEventState()
    data class Navigate(val route: String, val includeBackStack: Boolean = false): SettingEventState()
}