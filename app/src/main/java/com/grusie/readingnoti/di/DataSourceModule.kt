package com.grusie.readingnoti.di

import com.grusie.data.datasource.LocalTotalSettingDataSource
import com.grusie.data.datasource.TotalSettingDataSource
import com.grusie.data.datasourceImpl.LocalTotalSettingDataSourceImpl
import com.grusie.data.datasourceImpl.TotalSettingDataSourceImpl
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
}