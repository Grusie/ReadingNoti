package com.grusie.readingnoti.di

import com.grusie.domain.repository.TotalSettingRepository
import com.grusie.domain.usecase.totalSetting.ChangeSettingInfoUseCase
import com.grusie.domain.usecase.totalSetting.GetLocalPersonalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.GetLocalTotalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.GetPersonalSettingUseCase
import com.grusie.domain.usecase.totalSetting.InitPersonalSettingUseCase
import com.grusie.domain.usecase.totalSetting.InitTotalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.SetLocalPersonalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.SetPersonalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideTotalSettingUseCases(
        totalSettingRepository: TotalSettingRepository
    ): TotalSettingUseCases {
        return TotalSettingUseCases(
            initTotalSettingListUseCase = InitTotalSettingListUseCase(totalSettingRepository),
            getLocalTotalSettingListUseCase = GetLocalTotalSettingListUseCase(totalSettingRepository),
            initPersonalSettingUseCase = InitPersonalSettingUseCase(totalSettingRepository),
            getLocalPersonalSettingListUseCase = GetLocalPersonalSettingListUseCase(
                totalSettingRepository
            ),
            changeSettingInfoUseCase = ChangeSettingInfoUseCase(totalSettingRepository),
            getPersonalSettingUseCase = GetPersonalSettingUseCase(totalSettingRepository),
            setPersonalSettingListUseCase = SetPersonalSettingListUseCase(totalSettingRepository),
            setLocalPersonalSettingListUseCase = SetLocalPersonalSettingListUseCase(
                totalSettingRepository
            )
        )
    }

}