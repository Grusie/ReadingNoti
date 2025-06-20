package com.grusie.presentation.ui.setting

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.core.appSetting.AppPackageEnum
import com.grusie.core.appSetting.AppSettingFieldModel
import com.grusie.core.appSetting.BaseAppSetting
import com.grusie.core.appSetting.KakaoAppSetting
import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.usecase.storage.StorageUseCases
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.Routes
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
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
    val auth: FirebaseAuth
) : BaseViewModel() {
    private val _settingMergedList: MutableStateFlow<List<MergedSetting>> =
        MutableStateFlow(emptyList())
    val settingMergedList: StateFlow<List<MergedSetting>> = _settingMergedList.asStateFlow()

    private val _selectedAppItem: MutableStateFlow<BaseAppSetting?> = MutableStateFlow(null)
    val selectedAppItem: StateFlow<BaseAppSetting?> = _selectedAppItem.asStateFlow()

    private var selectedAppPersonalSetting: DomainPersonalSettingDto? = null

    private val _isBottomSheetVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible.asStateFlow()

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

            val totalSettingList = totalSettingListDeferred.await()
            val personalSettingList = personalSettingListDeferred.await()

            val totalSettingMap = totalSettingList.associateBy { it.menuId }
            val personalSettingMap = personalSettingList.associateBy { it.menuId }

            val mergedList = totalSettingMap.map { (menuId, totalSetting) ->
                val personalSetting = personalSettingMap[menuId]

                MergedSetting(
                    totalSetting = totalSetting,
                    personalSetting = personalSetting
                )
            }

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

        var updatedSetting: DomainPersonalSettingDto? = null

        _settingMergedList.update { list ->
            list.map { item ->
                if (item.totalSetting.menuId == menuId) {
                    val updatedPersonal = item.personalSetting?.copy(isEnabled = isSelected)
                    if (updatedPersonal != null) {
                        updatedSetting = updatedPersonal
                    }
                    item.copy(personalSetting = updatedPersonal)
                } else item
            }
        }

        updatedSetting?.let {
            totalSettingUseCases.changeSettingInfoUseCase(
                auth.currentUser?.uid,
                it
            )
        }

        setUiState(BaseUiState.Idle)
    }

    private fun onGeneralSettingClick(totalAppSetting: TOTAL_APP_SETTING) {
        when (totalAppSetting) {
            TOTAL_APP_SETTING.COLLECT_NOTI_ENABLED -> {}
            TOTAL_APP_SETTING.FOCUS_MODE -> {}
            TOTAL_APP_SETTING.BOOT_ENABLED -> {}
            TOTAL_APP_SETTING.TTS_ENABLED -> {}
            else -> {}
        }
    }

    private fun onAppSettingClick(appSetting: BaseAppSetting) {
        _selectedAppItem.value = appSetting
        setBottomDialogVisible(true)
    }

    fun onSettingClick(type: SettingType, data: Any) {
        when(type) {
            SettingType.GENERAL -> {
                val totalAppSetting = (data as? TOTAL_APP_SETTING)
                totalAppSetting?.let { onGeneralSettingClick(it) }
            }

            SettingType.APP -> {
                val appSetting = (data as? BaseAppSetting)
                appSetting?.let { onAppSettingClick(it) }
            }
        }
    }

    fun onChangedAppDetailSetting(appSettingFieldModel: AppSettingFieldModel, data: Any) {
        selectedAppPersonalSetting?.let {
            when(AppPackageEnum.from(it.packageName ?: "")) {
                AppPackageEnum.KAKAO -> {
                    when(appSettingFieldModel) {
                        KakaoAppSetting.KakaoAppSettingField.QuiteTtsEnabled -> {
                            _selectedAppItem.value = (_selectedAppItem.value as KakaoAppSetting).updateQuietTtsEnabled(data as Boolean)
                        }
                    }
                }
                else -> {
                    return
                }
            }

            selectedAppPersonalSetting = it.copy(customData = _selectedAppItem.value)
        }

        viewModelScope.launch {
            setUiState(BaseUiState.Loading)

            _settingMergedList.update { list ->
                list.map { item ->
                    if(item.personalSetting == selectedAppPersonalSetting) {
                        item.copy(personalSetting = selectedAppPersonalSetting)
                    } else item
                }
            }

            selectedAppPersonalSetting?.let {
                totalSettingUseCases.changeSettingInfoUseCase(
                    auth.currentUser?.uid,
                    it.copy(customData = _selectedAppItem.value)
                )
            }
            setUiState(BaseUiState.Idle)
        }
    }

    fun signOut() {
        auth.signOut()
        setEventState(BaseEventState.Navigate(Routes.SPLASH, includeBackStack = true))
    }

    fun setSelectedAppPersonalSetting(selectedAppPersonalSetting: DomainPersonalSettingDto) {
        this.selectedAppPersonalSetting = selectedAppPersonalSetting
    }

    fun setBottomDialogVisible(isVisible: Boolean) {
        _isBottomSheetVisible.value = isVisible
    }
}