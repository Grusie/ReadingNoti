package com.grusie.readingnoti.di

import com.grusie.data.repositoryImpl.TotalSettingRepositoryImpl
import com.grusie.domain.repository.TotalSettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTotalSettingRepository(
        impl: TotalSettingRepositoryImpl
    ): TotalSettingRepository
}