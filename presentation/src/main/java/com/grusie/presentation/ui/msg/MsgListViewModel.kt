package com.grusie.presentation.ui.msg

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.grusie.domain.data.DomainMsgData
import com.grusie.domain.usecase.msgData.MsgDataUseCases
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MsgListViewModel @Inject constructor(
    private val msgDataUseCases: MsgDataUseCases,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(){
    private val _msgList: MutableStateFlow<List<DomainMsgData>> = MutableStateFlow(emptyList())
    val msgList: StateFlow<List<DomainMsgData>> = _msgList.asStateFlow()

    private val appId = savedStateHandle.get<Int>(Routes.MsgKeys.EXTRA_APP_ID) ?: -1
    val appDisplayName = savedStateHandle.get<String>(Routes.MsgKeys.EXTRA_APP_NAME) ?: "UnKnown"

    private val _expandedIds: MutableStateFlow<Set<Long>> = MutableStateFlow(emptySet())
    val expandedIds: StateFlow<Set<Long>> = _expandedIds.asStateFlow()

    fun toggleExpand(id: Long) {
        _expandedIds.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    init {
        viewModelScope.launch {
            setUiState(BaseUiState.Loading)
            msgDataUseCases.observeMsgListUseCase(appId)
                .collect { msgList ->
                    _msgList.value = msgList
                    setUiState(BaseUiState.Idle)
                }
        }
    }

    fun deleteAllList() {
        viewModelScope.launch {
            msgDataUseCases.deleteAllMsgDataUseCase(appId)
        }
    }

    object ConfirmType {
        const val DELETE = 1
    }
}