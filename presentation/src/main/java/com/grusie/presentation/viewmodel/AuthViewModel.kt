package com.grusie.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.grusie.domain.data.CustomException
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.auth.LoginEventState
import com.grusie.presentation.ui.auth.LoginUiState
import com.grusie.presentation.ui.base.BaseViewModel
import com.grusie.presentation.utils.getErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val totalSettingUseCases: TotalSettingUseCases
) : BaseViewModel() {

    /**
     * idToken을 가지고 구글 로그인 진행
     *
     * @param idToken 구글 아이디 토큰
     */
    fun requestGoogleSignIn(idToken: String) {
        setUiState(LoginUiState.Loading)
        val authCredential = GoogleAuthProvider.getCredential(idToken, null)

        val googleSignInTask = auth.signInWithCredential(authCredential)

        googleSignInTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModelScope.launch {
                    if (initPersonalSetting()) {
                        setEventState(LoginEventState.Navigate(Routes.MAIN, true))
                    }
                }
            } else {
                setEventState(LoginEventState.Error(task.exception?.message ?: ""))
            }
            setUiState(LoginUiState.Idle)
        }
    }

    /**
     * 로그인 성공 시, 개인설정 값을 세팅하는 함수
     * 신규 사용자의 경우
     * -> 현재 localDB에 있는 설정 값을 넣음
     *
     * 기존 사용자의 경우
     * -> 현재 localDB에 있는 설정 값을 덮어쓸 것인지, 서버에서 가져온 걸로 localDb에 덮어쓸 것인지 팝업을 띄움
     *
     * @return 화면이동을 할 때에만 true 리턴
     */
    private suspend fun initPersonalSetting(): Boolean {
        auth.currentUser?.let {
            return totalSettingUseCases.getPersonalSettingUseCase(it.uid).fold(
                onSuccess = {
                    // 기존 사용자
                    setEventState(
                        LoginEventState.Alert(
                            "서버에 저장된 설정이 있습니다. 불러오시겠습니까?",
                            "불러오지 않으면 이 기기의 설정으로 서버 설정이 덮어쓰기 됩니다."
                        )
                    )
                    false
                }, onFailure = { e ->
                    when (e) {
                        is CustomException.NetworkError -> {
                            auth.signOut()
                            // 네트워크 에러
                            setEventState(LoginEventState.Error(context.getString(R.string.common_error_network)))
                        }

                        is CustomException.NotFoundOnServer -> {
                            // 서버에 데이터 없음 <- 신규 사용자
                            totalSettingUseCases.setPersonalSettingListUseCase(
                                it.uid,
                                totalSettingUseCases.getLocalPersonalSettingListUseCase()
                            )

                            return true
                        }

                        else -> {
                            auth.signOut()
                            setEventState(LoginEventState.Error(context.getString(R.string.common_error_unknown_msg)))
                        }
                    }
                    false
                })
        }
        auth.signOut()
        return false
    }

    fun coverPersonalSetting(isCover: Boolean) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            auth.currentUser?.let {
                if (isCover) {
                    totalSettingUseCases.getPersonalSettingUseCase(it.uid).onSuccess { list ->
                        totalSettingUseCases.setLocalPersonalSettingListUseCase(list)
                        setUiState(LoginUiState.Idle)
                        setEventState(LoginEventState.Navigate(Routes.MAIN, true))
                    }.onFailure { e ->
                        setUiState(LoginUiState.Idle)

                        auth.signOut()
                        setEventState(LoginEventState.Error(e.getErrorMsg(context)))
                    }
                } else {
                    totalSettingUseCases.setPersonalSettingListUseCase(
                        it.uid, totalSettingUseCases.getLocalPersonalSettingListUseCase()
                    ).onSuccess {
                        setUiState(LoginUiState.Idle)
                        setEventState(LoginEventState.Navigate(Routes.MAIN, true))
                    }.onFailure { e ->
                        setUiState(LoginUiState.Idle)

                        auth.signOut()
                        setEventState(LoginEventState.Error(e.getErrorMsg(context)))
                    }
                }
            }
        }
    }
}