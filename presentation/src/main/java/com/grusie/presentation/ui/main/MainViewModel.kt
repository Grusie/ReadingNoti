package com.grusie.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.core.utils.Logger
import com.grusie.domain.usecase.admin.AdminUseCases
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val adminUseCases: AdminUseCases
) : BaseViewModel() {
    private val _isAdmin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        isAdmin()
    }

    private fun isAdmin() {
        setUiState(MainUiState.Loading)

        viewModelScope.launch {
            adminUseCases.getAdminUserListUseCase().onSuccess { list ->
                list.firstOrNull { it.uid == auth.currentUser?.uid }?.let {
                    // 현재 계정이 어드민일 때
                    _isAdmin.emit(true)
                }
                setUiState(MainUiState.Idle)
            }.onFailure { e ->
                // 어드민 리스트를 불러오는 네트워크 에러는 처리 할 필요 없음
                Logger.e(this::class.simpleName.toString(), "getAdminUserList Error : ${e.message}")
                setUiState(MainUiState.Idle)
            }
        }
    }
}