package com.grusie.readingnoti.di

import com.grusie.core.utils.LoggerInterface
import com.grusie.readingnoti.BuildConfig
import com.grusie.readingnoti.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoggerModule {
    @Provides
    fun provideIsDebug(): Boolean = BuildConfig.DEBUG

    @Provides
    @Singleton
    fun provideLogger(
        isBuild: Boolean
    ): LoggerInterface = Logger(isBuild)
}