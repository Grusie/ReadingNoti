package com.grusie.presentation.ui.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.core.utils.Logger
import com.grusie.domain.usecase.user.UserUseCases
import com.grusie.presentation.ui.base.BaseViewModel
import com.grusie.presentation.utils.getErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val userUseCases: UserUseCases
) : BaseViewModel() {
    private val _isAdmin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        viewModelScope.launch {
            checkAdmin()
        }
    }

    private suspend fun checkAdmin() {

        viewModelScope.launch {
            setUiState(MainUiState.Loading)

            auth.currentUser?.let {
                userUseCases.isAdminUseCase(it.uid).onSuccess { isAdmin ->
                    _isAdmin.emit(isAdmin)
                }.onFailure { e ->
                    _isAdmin.emit(false)

                    // 어드민 리스트를 불러오는 네트워크 에러는 처리 할 필요 없음
                    Logger.e(
                        this::class.simpleName.toString(),
                        "getAdminUserList Error : ${e.getErrorMsg(context)}"
                    )
                }
            }
            setUiState(MainUiState.Idle)
        }
    }
}