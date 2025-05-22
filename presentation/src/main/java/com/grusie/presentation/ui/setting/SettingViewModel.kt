package com.grusie.presentation.ui.setting

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.usecase.storage.StorageUseCases
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.mapper.toUi
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val totalSettingUseCases: TotalSettingUseCases,
    private val storageUseCases: StorageUseCases,
    private val auth: FirebaseAuth
) : BaseViewModel() {
    private val _settingSwitchStates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val settingSwitchStates: StateFlow<Map<Int, Boolean>> = _settingSwitchStates.asStateFlow()

    private val _settingMergedList: MutableStateFlow<List<MergedSetting>> =
        MutableStateFlow(emptyList())
    val settingMergedList: StateFlow<List<MergedSetting>> = _settingMergedList.asStateFlow()

    init {
        requestTotalSettingList()
    }

    private fun requestTotalSettingList() {
        viewModelScope.launch {
            setUiState(BaseUiState.Loading)
            val totalSettingListDeferred =
                async { totalSettingUseCases.getLocalTotalSettingListUseCase() }
            val personalSettingListDeferred =
                async { totalSettingUseCases.getLocalPersonalSettingListUseCase() }

            val totalSettingList = totalSettingListDeferred.await().map { it.toUi() }
            val personalSettingList = personalSettingListDeferred.await()

            val totalSettingMap = totalSettingList.associateBy { it.menuId }
            val personalSettingMap = personalSettingList.associateBy { it.menuId }

            val newSwitchStates = mutableMapOf<Int, Boolean>()

            val mergedList = totalSettingMap.map { (menuId, totalSetting) ->
                val personalSetting = personalSettingMap[menuId]

                newSwitchStates[menuId] = personalSetting?.isEnabled ?: totalSetting.isInitEnabled

                MergedSetting(
                    totalSetting = totalSetting,
                    personalSetting = personalSetting
                )
            }
            _settingSwitchStates.emit(newSwitchStates)

            setUiState(BaseUiState.Idle)

            if (mergedList.isEmpty()) {
                // 설정 전체 리스트는 비어있으면 안 되는데 비어있는 경우가 발생한 것으로 에러로 표현
                setEventState(BaseEventState.Error("알 수 없는 에러가 발생했습니다."))
            } else {
                _settingMergedList.emit(mergedList)
            }
        }
    }

    suspend fun onSettingRadioButtonChanged(
        menuId: Int,
        isSelected: Boolean
    ) {
        setUiState(BaseUiState.Loading)

        totalSettingUseCases.changeSettingInfoUseCase(
            auth.currentUser?.uid,
            DomainPersonalSettingDto(menuId = menuId, isEnabled = isSelected)
        )
        _settingSwitchStates.update {
            _settingSwitchStates.value.toMutableMap().apply {
                this[menuId] = isSelected
            }
        }
        setUiState(BaseUiState.Idle)
    }

    suspend fun onSettingClick(totalAppSetting: TOTAL_APP_SETTING) {
        when (totalAppSetting) {
            TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED -> {}
            TOTAL_APP_SETTING.FOCUS_MODE -> {}
            TOTAL_APP_SETTING.BOOT_ENABLED -> {}
        }
    }
}