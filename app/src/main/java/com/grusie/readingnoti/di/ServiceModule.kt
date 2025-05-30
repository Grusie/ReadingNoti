package com.grusie.readingnoti.di

import com.grusie.core.common.TTSServiceController
import com.grusie.readingnoti.service.TTSServiceControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun bindTTSServiceController(
        impl: TTSServiceControllerImpl
    ): TTSServiceController
}