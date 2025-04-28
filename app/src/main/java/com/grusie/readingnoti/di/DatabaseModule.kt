package com.grusie.readingnoti.di

import android.content.Context
import androidx.room.Room
import com.grusie.data.AppDatabase
import com.grusie.data.dao.LocalTotalSettingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideTotalSettingDao(database: AppDatabase): LocalTotalSettingDao {
        return database.localTotalSettingDao()
    }
}