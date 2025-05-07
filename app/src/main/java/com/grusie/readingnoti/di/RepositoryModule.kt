package com.grusie.readingnoti.di

import com.grusie.data.repositoryImpl.StorageRepositoryImpl
import com.grusie.data.repositoryImpl.TotalSettingRepositoryImpl
import com.grusie.data.repositoryImpl.UserRepositoryImpl
import com.grusie.domain.repository.StorageRepository
import com.grusie.domain.repository.TotalSettingRepository
import com.grusie.domain.repository.UserRepository
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

    @Binds
    abstract fun bindStorageRepository(
        impl: StorageRepositoryImpl
    ): StorageRepository

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}