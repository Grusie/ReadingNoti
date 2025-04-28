package com.grusie.presentation.ui.setting

import androidx.lifecycle.viewModelScope
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
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
            val totalSettingList = totalSettingUseCases.getLocalTotalSettingListUseCase()
            setUiState(SettingUiState.Idle)

            setEventState(SettingEventState.Success(totalSettingList.map { it.toUi() }))
            if(totalSettingList.isEmpty()) {
                // 설정 전체 리스트는 비어있으면 안 되는데 비어있는 경우가 발생한 것으로 에러로 표현
                setEventState(SettingEventState.Error("알 수 없는 에러가 발생했습니다."))
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