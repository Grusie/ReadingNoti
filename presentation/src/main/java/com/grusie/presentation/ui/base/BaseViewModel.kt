package com.grusie.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grusie.core.utils.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private var currentViewModelClass: String = ""
    private val _uiState: MutableStateFlow<BaseUiState> = MutableStateFlow(BaseUiState.Idle)
    val uiState: StateFlow<BaseUiState> = _uiState.asStateFlow()

    private val _eventState: MutableSharedFlow<BaseEventState?> = MutableStateFlow(null)
    val eventState: SharedFlow<BaseEventState?> = _eventState.asSharedFlow()

    init {
        currentViewModelClass = this::class.simpleName ?: ""
    }

    /**
     * uiState 설정
     * 설정 시 로그 작성
     *
     * @param uiState 각 uiState <- BaseUiState를 상속
     */
    fun setUiState(uiState: BaseUiState){
        viewModelScope.launch {
            log(Logger.LogType.LOG_TYPE_I, "uiStateChanged (${_uiState.value} -> ${uiState})")
            _uiState.emit(uiState)
        }
    }

    /**
     * eventState 설정
     * 설정 시 로그 작성
     *
     * @param eventState 각 eventState <- BaseEventState를 상속
     */
    fun setEventState(eventState: BaseEventState){
        viewModelScope.launch {
            log(Logger.LogType.LOG_TYPE_I, "eventTypeChanged (${eventState::class.qualifiedName} : ${eventState})")
            _eventState.emit(eventState)
        }
    }

    /**
     * 메세지만 받았을 때에는 LOG_TYPE_D로 자동 지정
     *
     * @param message 로그 메세지
     */
    fun log(message: String) {
        log(Logger.LogType.LOG_TYPE_D, message)
    }

    /**
     * 로그 타입과 메세지를 받아 로그를 찍어냄
     *
     * @param logType 로그 타입 (Logger.LogType 참조)
     * @param message 로그 메세지
     */
    fun log(logType: Logger.LogType, message: String) {
        Logger.log(logType, currentViewModelClass, message)
    }

    /**
     * Exception을 통째로 넘겨서 로그를 찍어냄
     *
     * @param exception Exception
     */
    fun log(exception: Exception) {
        Logger.logException(currentViewModelClass, exception)
    }
}