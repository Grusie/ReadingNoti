package com.grusie.presentation.ui.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.core.common.TotalMenu
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.domain.usecase.user.UserUseCases
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.base.BaseViewModel
import com.grusie.presentation.utils.getErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val userUseCases: UserUseCases,
    private val totalSettingUseCases: TotalSettingUseCases
) : BaseViewModel() {
    private val _isAdmin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private var job: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _personalSettingList: MutableStateFlow<List<DomainPersonalSettingDto>> = MutableStateFlow(
        emptyList()
    )
    val personalSettingList: StateFlow<List<DomainPersonalSettingDto>> = _personalSettingList.asStateFlow()

    val isTotalNotiEnabled: StateFlow<Boolean> = _personalSettingList.map { list ->
        list.find { it.menuId == TotalMenu.TOTAL_NOTI_ENABLED.menuId }?.isEnabled == true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), false)

    init {
        viewModelScope.launch {
            checkAdmin()

            if (job?.isActive != true) {
                job = serviceScope.launch {
                    totalSettingUseCases.observeLocalPersonalSettingsUseCase()
                        .distinctUntilChanged()
                        .flowOn(Dispatchers.IO)
                        .collect { settings ->
                            log("Change RoomDB : $settings")
                            _personalSettingList.emit(settings)
                        }
                }
            }
        }
    }

    private suspend fun checkAdmin() {

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