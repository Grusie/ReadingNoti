package com.grusie.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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

            val totalSettingJob = async { totalSettingUseCases.initTotalSettingListUseCase() }
            val personalSettingJob = async { initPersonalSetting() }

            totalSettingJob.await()
            personalSettingJob.await()

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

    private suspend fun initPersonalSetting() {
        // 만약 personalSetting이 LocalDB에 있다면, 해당 데이터를 서버에 전송하고, 없다면 서버에서 불러와서 넣음
        // 서버에도 없을 경우 기본 값을 불러 올 것

        try {
            totalSettingUseCases.initPersonalSettingUseCase(auth.currentUser?.uid)
        } catch (e: Exception) {
            // 로그인이 되어 있고, 로컬DB에 값이 없고, 서버통신을 진행하려는데 에러가 발생한 경우
            // 이런 경우에만 유일하게 앱을 실행하지 못 하도록 처리
            setEventState(SplashEventState.Error("로그인 처리 과정에서 에러가 발생했습니다.", true))
        }
    }
}