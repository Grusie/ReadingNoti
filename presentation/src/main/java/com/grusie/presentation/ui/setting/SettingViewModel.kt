package com.grusie.presentation.ui.setting

import androidx.lifecycle.viewModelScope
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.data.setting.BaseSettingMenu
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.mapper.toUi
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val totalSettingUseCases: TotalSettingUseCases
) : BaseViewModel() {

    init {
        requestTotalSettingList()
    }

    private fun requestTotalSettingList() {
        viewModelScope.launch {
            setUiState(SettingUiState.Loading)
            val result = totalSettingUseCases.getTotalSettingListUseCase()
            setUiState(SettingUiState.Idle)
            result.onSuccess { list ->
                setEventState(SettingEventState.Success(list.map { it.toUi() }))
            }.onFailure {
                setEventState(SettingEventState.Error(it.message ?: ""))
            }
        }
    }

    fun onSettingRadioButtonChanged(totalAppSetting: TOTAL_APP_SETTING): Boolean {
        return when (totalAppSetting) {
            TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED -> {false}
            TOTAL_APP_SETTING.FOCUS_MODE -> {false}
            TOTAL_APP_SETTING.BOOT_ENABLED -> {false}
        }
    }

    fun onSettingClick(totalAppSetting: TOTAL_APP_SETTING): Boolean {
        return when (totalAppSetting) {
            TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED -> {false}
            TOTAL_APP_SETTING.FOCUS_MODE -> {false}
            TOTAL_APP_SETTING.BOOT_ENABLED -> {false}
        }
    }
}