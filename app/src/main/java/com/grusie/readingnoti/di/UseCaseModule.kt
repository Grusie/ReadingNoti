package com.grusie.readingnoti.di

import com.grusie.domain.repository.StorageRepository
import com.grusie.domain.repository.TotalSettingRepository
import com.grusie.domain.repository.UserRepository
import com.grusie.domain.usecase.storage.StorageUseCases
import com.grusie.domain.usecase.storage.UploadFileToStorageUseCase
import com.grusie.domain.usecase.totalSetting.ChangeSettingInfoUseCase
import com.grusie.domain.usecase.totalSetting.DeleteTotalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.GetLocalPersonalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.GetLocalTotalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.GetPersonalSettingUseCase
import com.grusie.domain.usecase.totalSetting.GetServerTotalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.InitPersonalSettingUseCase
import com.grusie.domain.usecase.totalSetting.InitTotalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.SaveLocalTotalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.SetLocalPersonalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.SetPersonalSettingListUseCase
import com.grusie.domain.usecase.totalSetting.SetTotalSettingUseCase
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.domain.usecase.totalSetting.UpdateTotalSettingVisibilityUseCase
import com.grusie.domain.usecase.user.GetUserListUseCase
import com.grusie.domain.usecase.user.InitUserUseCase
import com.grusie.domain.usecase.user.IsAdminUseCase
import com.grusie.domain.usecase.user.SetAdminUseCase
import com.grusie.domain.usecase.user.UserUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideTotalSettingUseCases(
        totalSettingRepository: TotalSettingRepository
    ): TotalSettingUseCases {
        return TotalSettingUseCases(
            getServerTotalSettingListUseCase = GetServerTotalSettingListUseCase(
                totalSettingRepository
            ),
            initTotalSettingListUseCase = InitTotalSettingListUseCase(totalSettingRepository),
            getLocalTotalSettingListUseCase = GetLocalTotalSettingListUseCase(totalSettingRepository),
            initPersonalSettingUseCase = InitPersonalSettingUseCase(totalSettingRepository),
            getLocalPersonalSettingListUseCase = GetLocalPersonalSettingListUseCase(
                totalSettingRepository
            ),
            changeSettingInfoUseCase = ChangeSettingInfoUseCase(totalSettingRepository),
            getPersonalSettingUseCase = GetPersonalSettingUseCase(totalSettingRepository),
            setPersonalSettingListUseCase = SetPersonalSettingListUseCase(totalSettingRepository),
            setLocalPersonalSettingListUseCase = SetLocalPersonalSettingListUseCase(
                totalSettingRepository
            ),
            updateTotalSettingVisibilityUseCase = UpdateTotalSettingVisibilityUseCase(
                totalSettingRepository
            ),
            setTotalSettingUseCase = SetTotalSettingUseCase(totalSettingRepository),
            saveLocalTotalSettingListUseCase = SaveLocalTotalSettingListUseCase(
                totalSettingRepository
            ),
            deleteTotalSettingListUseCase = DeleteTotalSettingListUseCase(totalSettingRepository)
        )
    }

    @Provides
    fun provideStorageUseCases(
        storageRepository: StorageRepository
    ): StorageUseCases {
        return StorageUseCases(
            uploadFileToStorageUseCase = UploadFileToStorageUseCase(storageRepository)
        )
    }

    @Provides
    fun provideUserUseCases(
        userRepository: UserRepository
    ): UserUseCases {
        return UserUseCases(
            getUserListUseCase = GetUserListUseCase(userRepository),
            isAdminUseCase = IsAdminUseCase(userRepository),
            setAdminUseCase = SetAdminUseCase(userRepository),
            initUserUseCase = InitUserUseCase(userRepository)
        )
    }
}