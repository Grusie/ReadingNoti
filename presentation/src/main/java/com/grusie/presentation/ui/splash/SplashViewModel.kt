package com.grusie.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val totalSettingUseCases: TotalSettingUseCases
) : BaseViewModel() {
    companion object {
        const val SPLASH_TIME = 1000
    }

    init {
        initSetting()
    }

    private fun initSetting() {
        viewModelScope.launch {
            setUiState(SplashUiState.Loading)

            val startTime = System.currentTimeMillis() // 서버 통신 시작 시간 기록
            totalSettingUseCases.initTotalSettingListUseCase()

            val elapsedTime = System.currentTimeMillis() - startTime // 경과 시간 계산

            // 스플래시를 최소 1초는 진행하고 싶기에 코드 추가
            if (elapsedTime < SPLASH_TIME) {
                delay(SPLASH_TIME - elapsedTime) // 부족한 시간만큼 delay
            }

            if (auth.currentUser != null) {
                setEventState(SplashEventState.Navigate(Routes.MAIN, true))
            } else {
                setEventState(SplashEventState.Navigate(Routes.LOGIN, true))
            }

            setUiState(SplashUiState.Idle)
        }
    }
}