package com.grusie.presentation.ui.msg

import androidx.lifecycle.viewModelScope
import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MsgAppListViewModel @Inject constructor(
    private val totalSettingUseCases: TotalSettingUseCases
) : BaseViewModel(){
    private val _appList: MutableStateFlow<List<DomainTotalSettingDto>> = MutableStateFlow(emptyList())
    val appList: StateFlow<List<DomainTotalSettingDto>> = _appList.asStateFlow()

    init {
        viewModelScope.launch {
            _appList.value = totalSettingUseCases.getLocalTotalSettingListUseCase().filter { it.type == SettingType.APP }
        }
    }
}