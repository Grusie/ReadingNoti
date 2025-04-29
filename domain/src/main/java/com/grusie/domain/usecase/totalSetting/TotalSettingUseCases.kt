package com.grusie.domain.usecase.totalSetting

data class TotalSettingUseCases(
    val initTotalSettingListUseCase: InitTotalSettingListUseCase,
    val getLocalTotalSettingListUseCase: GetLocalTotalSettingListUseCase,
    val deleteLocalTotalSettingUseCase: DeleteLocalTotalSettingUseCase,
    val initPersonalSettingUseCase: InitPersonalSettingUseCase,
    val getLocalPersonalSettingListUseCase: GetLocalPersonalSettingListUseCase,
    val changeSettingInfoUseCase: ChangeSettingInfoUseCase
)