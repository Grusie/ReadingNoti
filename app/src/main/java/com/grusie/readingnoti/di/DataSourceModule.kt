package com.grusie.readingnoti.di

import com.grusie.data.datasource.LocalTotalSettingDataSource
import com.grusie.data.datasource.StorageDataSource
import com.grusie.data.datasource.TotalSettingDataSource
import com.grusie.data.datasource.UserDataSource
import com.grusie.data.datasourceImpl.LocalTotalSettingDataSourceImpl
import com.grusie.data.datasourceImpl.StorageDataSourceImpl
import com.grusie.data.datasourceImpl.TotalSettingDataSourceImpl
import com.grusie.data.datasourceImpl.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindTotalSettingDataSource(impl: TotalSettingDataSourceImpl): TotalSettingDataSource

    @Binds
    abstract fun bindLocalTotalSettingDataSource(impl: LocalTotalSettingDataSourceImpl): LocalTotalSettingDataSource

    @Binds
    abstract fun bindStorageDataSource(impl: StorageDataSourceImpl): StorageDataSource

    @Binds
    abstract fun bindUserDataSource(impl: UserDataSourceImpl): UserDataSource
}