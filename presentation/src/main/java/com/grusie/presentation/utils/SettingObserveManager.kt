package com.grusie.presentation.utils

import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.data.setting.MergedSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * 설정 정보를 Observing 해주는 매니저 객체
 *
 * 초기화는 Application에서만 사용할 것
 * StateFlow로 mergedSettingMap (MenuId to (Total, Personal))를 Observing
 * 다른 곳에서 사용하기 편하도록 App, General을 따로 분리하여 선언해 둠
 */
object SettingObserveManager {
    private val _mergedSettingMap = MutableStateFlow<Map<Int, MergedSetting>>(emptyMap())
    val mergedSettingMap: StateFlow<Map<Int, MergedSetting>> = _mergedSettingMap.asStateFlow()

    private val _mergedAppSettingMap = MutableStateFlow<Map<Int, MergedSetting>>(emptyMap())
    val mergedAppSettingMap: StateFlow<Map<Int, MergedSetting>> = _mergedAppSettingMap.asStateFlow()

    private val _mergedGeneralSettingMap = MutableStateFlow<Map<Int, MergedSetting>>(emptyMap())
    val mergedGeneralSettingMap: StateFlow<Map<Int, MergedSetting>> = _mergedGeneralSettingMap.asStateFlow()

    private var initialized = false

    fun init(scope: CoroutineScope, useCase: TotalSettingUseCases) {
        if (initialized) return

        scope.launch {
            observeMergedSettings(
                useCase.observeLocalTotalSettingsUseCase(),
                useCase.observeLocalPersonalSettingsUseCase()
            ).collect { mergedMap ->
                _mergedSettingMap.value = mergedMap

                _mergedAppSettingMap.value = mergedMap.filterValues {
                    it.totalSetting.type == SettingType.APP
                }

                _mergedGeneralSettingMap.value = mergedMap.filterValues {
                    it.totalSetting.type == SettingType.GENERAL
                }
            }
        }
    }

    private fun observeMergedSettings(
        totalSettingFlow: Flow<List<DomainTotalSettingDto>>,
        personalSettingFlow: Flow<List<DomainPersonalSettingDto>>
    ): Flow<Map<Int, MergedSetting>> {
        return totalSettingFlow.combine(personalSettingFlow) { totalList, personalList ->
            getMergedSettingMap(totalList, personalList)
        }
    }

    private fun getMergedSettingMap(
        totalSettingList: List<DomainTotalSettingDto>,
        personalSettings: List<DomainPersonalSettingDto>
    ): Map<Int, MergedSetting> {
        val totalSettingMap = totalSettingList.associateBy { it.menuId }
        val personalSettingMap = personalSettings.associateBy { it.menuId }

        return totalSettingMap.mapValues { (menuId, totalSetting) ->
            val personalSetting = personalSettingMap[menuId]

            MergedSetting(
                totalSetting = totalSetting,
                personalSetting = personalSetting
            )
        }
    }
}