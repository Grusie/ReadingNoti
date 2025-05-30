package com.grusie.readingnoti

import com.grusie.core.common.SettingType
import com.grusie.core.utils.LoggerProvider
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

object SettingObserveManager {
    private val mergedGeneralSettingMap: MutableMap<Int, MergedSetting> = mutableMapOf()
    private val mergedAppSettingMap: MutableMap<Int, MergedSetting> = mutableMapOf()

    private val _mergedSettingMap = MutableStateFlow<Map<Int, MergedSetting>>(emptyMap())
    val mergedSettingMap: StateFlow<Map<Int, MergedSetting>> = _mergedSettingMap.asStateFlow()

    fun init(scope: CoroutineScope, useCase: TotalSettingUseCases) {
        scope.launch {
            observeMergedSettings(
                useCase.observeLocalTotalSettingsUseCase(),
                useCase.observeLocalPersonalSettingsUseCase()
            ).collect {
                _mergedSettingMap.value = it
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

    private suspend fun refreshMergedSetting(
        totalSettingList: List<DomainTotalSettingDto>,
        personalSettings: List<DomainPersonalSettingDto>
    ) {
        try {
            val totalSettingMap = totalSettingList.associateBy { it.menuId }
            val personalSettingMap = personalSettings.associateBy { it.menuId }

            totalSettingMap.map { (menuId, totalSetting) ->
                val personalSetting = personalSettingMap[menuId]
                val mergedSetting = MergedSetting(
                    totalSetting = totalSetting,
                    personalSetting = personalSetting
                )

                if (totalSetting.type == SettingType.GENERAL) {
                    mergedGeneralSettingMap[menuId] = mergedSetting
                } else {
                    mergedAppSettingMap[menuId] = mergedSetting
                }
            }
        } catch (e: Exception) {
            LoggerProvider.logger.e("${this::class.simpleName}", "refreshMergedSetting Error")
        }
    }
}