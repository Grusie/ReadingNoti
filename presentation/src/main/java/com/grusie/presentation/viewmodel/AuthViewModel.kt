package com.grusie.presentation.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseViewModel
import com.grusie.presentation.ui.auth.LoginEventState
import com.grusie.presentation.ui.auth.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel() {

    /**
     * idToken을 가지고 구글 로그인 진행
     *
     * @param idToken 구글 아이디 토큰
     */
    fun requestGoogleSignIn(idToken: String) {
        setUiState(LoginUiState.Loading)
        val authCredential = GoogleAuthProvider.getCredential(idToken, null)

        setUiState(LoginUiState.Loading)
        val googleSignInTask = auth.signInWithCredential(authCredential)

        googleSignInTask.addOnCompleteListener { task ->
            setUiState(LoginUiState.Idle)
            if (task.isSuccessful) {
                setEventState(LoginEventState.Navigate(Routes.MAIN, true))
            } else {
                setEventState(LoginEventState.Error(task.exception?.message ?: ""))
            }
        }
    }
}