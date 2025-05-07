package com.grusie.presentation.ui.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.usecase.storage.StorageUseCases
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
class AdminViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storageUseCases: StorageUseCases,
    private val userUseCases: UserUseCases
) : BaseViewModel() {
    private val _userList: MutableStateFlow<List<DomainUserDto>> = MutableStateFlow(emptyList())
    val userList: StateFlow<List<DomainUserDto>> = _userList.asStateFlow()

    fun getUserList() {
        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            userUseCases.getUserListUseCase().onSuccess { list ->
                _userList.value = list.sortedWith(
                    compareByDescending<DomainUserDto> { it.isAdmin }.thenBy { it.name }
                )
            }.onFailure { e ->
                setEventState(AdminEventState.Error(errorMsg = e.getErrorMsg(context)))
            }
            setUiState(AdminUiState.Idle)
        }
    }

    fun setAdmin(uid: String, isAdmin: Boolean) {
        viewModelScope.launch {
            setUiState(AdminUiState.Loading)

            if (uid == auth.currentUser?.uid && !isAdmin) {
                setEventState(AdminEventState.Error(errorMsg = "본인은 제외 할 수 없습니다."))
            } else {
                userUseCases.setAdminUseCase(uid, isAdmin).onSuccess {
                    getUserList()
                }.onFailure { e ->
                    setEventState(AdminEventState.Error(errorMsg = e.getErrorMsg(context)))
                }
            }
            setUiState(AdminUiState.Idle)
        }
    }

    fun uploadImageToStorage(path: String, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return

        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            storageUseCases.uploadFileToStorageUseCase(path = path, bytes = bytes).onSuccess {
                setEventState(AdminEventState.Error(errorMsg = "성공"))
            }.onFailure { e ->
                setEventState(AdminEventState.Error(errorMsg = e.getErrorMsg(context)))
            }
            setUiState(AdminUiState.Idle)
        }
    }
}