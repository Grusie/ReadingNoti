package com.grusie.domain.usecase.totalSetting

data class TotalSettingUseCases(
    val initTotalSettingListUseCase: InitTotalSettingListUseCase,
    val getLocalTotalSettingListUseCase: GetLocalTotalSettingListUseCase,
    val initPersonalSettingUseCase: InitPersonalSettingUseCase,
    val getLocalPersonalSettingListUseCase: GetLocalPersonalSettingListUseCase,
    val changeSettingInfoUseCase: ChangeSettingInfoUseCase,
    val getPersonalSettingUseCase: GetPersonalSettingUseCase,
    val setPersonalSettingListUseCase: SetPersonalSettingListUseCase,
    val setLocalPersonalSettingListUseCase: SetLocalPersonalSettingListUseCase
)