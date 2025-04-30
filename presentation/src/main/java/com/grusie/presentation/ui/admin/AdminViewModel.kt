package com.grusie.presentation.ui.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.domain.usecase.storage.StorageUseCases
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storageUseCases: StorageUseCases
) : BaseViewModel() {

    fun uploadImageToStorage(path: String, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return

        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            storageUseCases.uploadFileToStorageUseCase(path = path, bytes = bytes).onSuccess {
                setEventState(AdminEventState.Error(errorMsg = "성공"))
            }.onFailure { e ->
                setEventState(AdminEventState.Error(errorMsg = e.message ?: ""))
            }
            setUiState(AdminUiState.Idle)
        }
    }
}