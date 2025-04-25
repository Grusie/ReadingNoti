package com.grusie.readingnoti.di

import com.grusie.domain.usecase.totalSetting.GetTotalSettingListUseCase
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
        getTotalSettingListUseCase: GetTotalSettingListUseCase
    ): TotalSettingUseCases {
        return TotalSettingUseCases(
            getTotalSettingListUseCase = getTotalSettingListUseCase
        )
    }

}