package com.grusie.readingnoti.di

import android.content.Context
import com.grusie.core.utils.NetworkChecker
import com.grusie.readingnoti.DefaultNetworkChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkChecker(
        @ApplicationContext context: Context
    ): NetworkChecker = DefaultNetworkChecker(context)
}