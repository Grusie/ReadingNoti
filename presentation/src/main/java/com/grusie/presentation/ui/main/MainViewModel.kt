package com.grusie.presentation.ui.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.core.common.TotalMenu
import com.grusie.domain.data.DomainMsgData
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.usecase.msgData.MsgDataUseCases
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.domain.usecase.user.UserUseCases
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.base.BaseViewModel
import com.grusie.presentation.utils.SettingObserveManager
import com.grusie.presentation.utils.getErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val userUseCases: UserUseCases,
    private val totalSettingUseCases: TotalSettingUseCases,
    private val msgDataUseCases: MsgDataUseCases
) : BaseViewModel() {
    private val _isAdmin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _mergedAppSettingMap = MutableStateFlow<Map<Int, MergedSetting>>(emptyMap())
    val mergedAppSettingMap: StateFlow<Map<Int, MergedSetting>> = _mergedAppSettingMap.asStateFlow()

    private val _mergedGeneralSettingMap = MutableStateFlow<Map<Int, MergedSetting>>(emptyMap())
    val mergedGeneralSettingMap: StateFlow<Map<Int, MergedSetting>> =
        _mergedGeneralSettingMap.asStateFlow()

    private val _msgDataList: MutableStateFlow<List<DomainMsgData>> = MutableStateFlow(emptyList())
    val msgDataList: StateFlow<List<DomainMsgData>> = _msgDataList.asStateFlow()

    private val _isEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    init {
        checkAdmin()
        collectMergedSettingMap()
        collectMsgList()
    }

    private fun collectMergedSettingMap() {
        viewModelScope.launch {
            combine(
                SettingObserveManager.mergedGeneralSettingMap,
                SettingObserveManager.mergedAppSettingMap
            ) { general, app ->
                general to app
            }.collectLatest { (general, app) ->
                _mergedGeneralSettingMap.value = general
                _mergedAppSettingMap.value = app

                _isEnabled.value =
                    general[TotalMenu.COLLECT_NOTI_ENABLED.menuId]?.personalSetting?.isEnabled == true
            }
        }
    }

    private fun collectMsgList(){
        viewModelScope.launch {
            msgDataUseCases.observeMsgListUseCase(menuId = null)
                .collect { msgList ->
                    _msgDataList.value = msgList
                }
        }
    }

    private fun checkAdmin() {
        viewModelScope.launch {
            setUiState(BaseUiState.Loading)

            auth.currentUser?.let {
                userUseCases.isAdminUseCase(it.uid).onSuccess { isAdmin ->
                    _isAdmin.emit(isAdmin)
                }.onFailure { e ->
                    _isAdmin.emit(false)

                    // 어드민 리스트를 불러오는 네트워크 에러는 처리 할 필요 없음
                    log("getAdminUserList Error : ${e.getErrorMsg(context)}")
                }
            }
            setUiState(BaseUiState.Idle)
        }
    }

    fun changeEnabled(
        menuId: Int,
        isEnabled: Boolean
    ) {
        viewModelScope.launch {
            setUiState(BaseUiState.Loading)

            totalSettingUseCases.changeSettingInfoUseCase(
                auth.currentUser?.uid,
                DomainPersonalSettingDto(menuId = menuId, isEnabled = isEnabled)
            )

            setUiState(BaseUiState.Idle)
        }
    }
}