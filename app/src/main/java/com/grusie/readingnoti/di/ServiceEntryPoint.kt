package com.grusie.readingnoti.di

import com.grusie.core.common.TTSServiceController
import com.grusie.core.utils.LoggerInterface
import com.grusie.domain.usecase.msgData.MsgDataUseCases
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotiRecvServiceEntryPoint {
    fun totalSettingUseCases(): TotalSettingUseCases
    fun logger(): LoggerInterface
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MainServiceEntryPoint {
    fun msgDataUseCases(): MsgDataUseCases
    fun logger(): LoggerInterface
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun totalSettingUseCases(): TotalSettingUseCases
    fun ttsServiceController(): TTSServiceController
}