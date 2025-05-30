package com.grusie.presentation.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.grusie.core.utils.NetworkChecker
import com.grusie.domain.data.AuthException
import com.grusie.domain.data.CommonException
import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.domain.usecase.user.UserUseCases
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.base.BaseViewModel
import com.grusie.presentation.utils.getErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val networkChecker: NetworkChecker,
    private val totalSettingUseCases: TotalSettingUseCases,
    private val userUseCases: UserUseCases
) : BaseViewModel() {
    private val _idText: MutableStateFlow<String> = MutableStateFlow("")
    val idText: StateFlow<String> = _idText.asStateFlow()

    private val _pwText: MutableStateFlow<String> = MutableStateFlow("")
    val pwText: StateFlow<String> = _pwText.asStateFlow()

    private val _pwConfirmText: MutableStateFlow<String> = MutableStateFlow("")
    val pwConfirmText: StateFlow<String> = _pwConfirmText.asStateFlow()

    private val _nickNameText: MutableStateFlow<String> = MutableStateFlow("")
    val nickNameText: StateFlow<String> = _nickNameText.asStateFlow()

    private val _isPasswordVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _isPasswordConfirmVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPasswordConfirmVisible: StateFlow<Boolean> = _isPasswordConfirmVisible.asStateFlow()

    /**
     * idToken을 가지고 구글 로그인 진행
     *
     * @param idToken 구글 아이디 토큰
     */
    fun requestGoogleSignIn(idToken: String) {

        if(!networkChecker.isNetworkAvailable()){
            setEventState(BaseEventState.Error(CommonException.NetworkError.getErrorMsg(context)))
            return
        }

        setUiState(BaseUiState.Loading)
        val authCredential = GoogleAuthProvider.getCredential(idToken, null)

        val googleSignInTask = auth.signInWithCredential(authCredential)

        googleSignInTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccessLogin()
            } else {
                setEventState(BaseEventState.Error(task.exception?.getErrorMsg(context) ?: context.getString(R.string.common_error_unknown_msg)))
            }
            setUiState(BaseUiState.Idle)
        }.addOnFailureListener {
            setUiState(BaseUiState.Idle)
        }
    }

    private fun onSuccessLogin() {
        viewModelScope.launch {
            auth.currentUser?.let {
                userUseCases.initUserUseCase(
                    DomainUserDto(
                        uid = it.uid,
                        email = it.email ?: it.uid,
                        isAdmin = false,
                        name = it.displayName ?: it.email ?: ""
                    )
                )
            }?.onSuccess {
                if (initPersonalSetting()) {
                    setEventState(BaseEventState.Navigate(Routes.MAIN, true))
                }
            }?.onFailure { e ->
                setEventState(BaseEventState.Error(e.getErrorMsg(context)))
            } ?: run { setEventState(BaseEventState.Error(context.getString(R.string.common_error_unknown_msg))) }
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
                        BaseEventState.Confirm(
                            context.getString(R.string.str_cover_title),
                            context.getString(R.string.str_cover_description),
                            confirmType = ConfirmType.COVER
                        )
                    )
                    false
                }, onFailure = { e ->
                    when (e) {
                        is CommonException.NetworkError -> {
                            auth.signOut()
                            // 네트워크 에러
                            setEventState(BaseEventState.Error(context.getString(R.string.common_error_network)))
                        }

                        is CommonException.NotFoundOnServer -> {
                            // 서버에 데이터 없음 <- 신규 사용자
                            totalSettingUseCases.setPersonalSettingListUseCase(
                                it.uid,
                                totalSettingUseCases.getLocalPersonalSettingListUseCase()
                            )

                            return true
                        }

                        else -> {
                            auth.signOut()
                            setEventState(BaseEventState.Error(context.getString(R.string.common_error_unknown_msg)))
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
            setUiState(BaseUiState.Loading)
            auth.currentUser?.let {
                if (isCover) {
                    totalSettingUseCases.getPersonalSettingUseCase(it.uid).onSuccess { list ->
                        totalSettingUseCases.setLocalPersonalSettingListUseCase(list)
                        setUiState(BaseUiState.Idle)
                        setEventState(BaseEventState.Navigate(Routes.MAIN, true))
                    }.onFailure { e ->
                        setUiState(BaseUiState.Idle)

                        auth.signOut()
                        setEventState(BaseEventState.Error(e.getErrorMsg(context)))
                    }
                } else {
                    totalSettingUseCases.setPersonalSettingListUseCase(
                        it.uid, totalSettingUseCases.getLocalPersonalSettingListUseCase()
                    ).onSuccess {
                        setUiState(BaseUiState.Idle)
                        setEventState(BaseEventState.Navigate(Routes.MAIN, true))
                    }.onFailure { e ->
                        setUiState(BaseUiState.Idle)

                        auth.signOut()
                        setEventState(BaseEventState.Error(e.getErrorMsg(context)))
                    }
                }
            }
        }
    }

    fun emailLogin() {
        if (_idText.value.isEmpty() || _pwText.value.isEmpty()) {
            setEventState(BaseEventState.Error(CommonException.EssentialError.getErrorMsg(context)))
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(_idText.value).matches()) {
            // 이메일이 이메일 형식이 아닐 경우
            setEventState(
                BaseEventState.Error(AuthException.EmailTypeMatchingError.getErrorMsg(context))
            )
            return
        }

        if(!networkChecker.isNetworkAvailable()){
            setEventState(BaseEventState.Error(CommonException.NetworkError.getErrorMsg(context)))
            return
        }

        setUiState(BaseUiState.Loading)
        auth.signInWithEmailAndPassword(_idText.value, _pwText.value).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                onSuccessLogin()
            } else {
                val exception = task.exception
                if(exception is FirebaseAuthInvalidCredentialsException) {
                    setEventState(BaseEventState.Error(AuthException.EmailPwIncorrectError.getErrorMsg(context)))
                } else {
                    setEventState(
                        BaseEventState.Error(
                            task.exception?.getErrorMsg(context)
                                ?: context.getString(R.string.common_error_unknown_msg)
                        )
                    )
                }
            }
            setUiState(BaseUiState.Idle)
        }.addOnFailureListener {
            setUiState(BaseUiState.Idle)
        }
    }

    fun emailSignUp() {
        if (!isPossibleSignUp()) return
        if (!networkChecker.isNetworkAvailable()) setEventState(BaseEventState.Error(CommonException.NetworkError.getErrorMsg(context)))

        setUiState(BaseUiState.Loading)
        auth.createUserWithEmailAndPassword(_idText.value, _pwText.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.signOut()
                    setEventState(
                        BaseEventState.Alert(
                            title = context.getString(R.string.common_error_title_notice_msg),
                            msg = context.getString(R.string.str_success_sign_up),
                            onConfirm = { setEventState(BaseEventState.PopBackStack) }
                        )
                    )
                } else {
                    if(task.exception is FirebaseAuthUserCollisionException) {
                        setEventState(BaseEventState.Error(errorMsg = AuthException.DuplicationEmailError.getErrorMsg(context)))
                    } else {
                        setEventState(
                            BaseEventState.Error(
                                errorMsg = task.exception?.getErrorMsg(context)
                                    ?: context.getString(R.string.common_error_unknown_msg)
                            )
                        )
                    }
                }
                setUiState(BaseUiState.Idle)
            }.addOnFailureListener {
                setUiState(BaseUiState.Idle)
            }
    }

    private fun isPossibleSignUp(): Boolean {
        if (_nickNameText.value.isEmpty() || _idText.value.isEmpty() || _pwText.value.isEmpty() || _pwConfirmText.value.isEmpty()) {
            // 닉네임, 이메일, 비밀번호, 비밀번호 변경 텍스트가 비어 있을 경우
            setEventState(BaseEventState.Error(CommonException.EssentialError.getErrorMsg(context)))
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(_idText.value).matches()) {
            // 이메일이 이메일 형식이 아닐 경우
            setEventState(
                BaseEventState.Error(
                    AuthException.EmailTypeMatchingError.getErrorMsg(
                        context
                    )
                )
            )
            return false
        }

        if (_pwText.value != _pwConfirmText.value) {
            // 비밀번호와 비밀번호 확인이 다를 경우
            setEventState(
                BaseEventState.Error(
                    AuthException.PwConfirmIncorrectError.getErrorMsg(
                        context
                    )
                )
            )
            return false
        }

        if (!isPossiblePassword()) {
            // 비밀번호 설정 규칙에 부합하지 않을 경우
            return false
        }

        return true
    }

    /**
     * 비밀번호 설정 규칙에 부합한지를 반환
     */
    private fun isPossiblePassword(): Boolean {

        if(_pwText.value.length < 6) {
            // 6자리 미만일 경우
            setEventState(
                BaseEventState.Error(
                    AuthException.PwLengthError.getErrorMsg(
                        context
                    )
                )
            )
            return false
        }

        return true
    }

    fun skipLogin() {
        setEventState(BaseEventState.Navigate(Routes.PERMISSION, true))
    }

    fun changePasswordVisible() {
        viewModelScope.launch {
            _isPasswordVisible.emit(!_isPasswordVisible.value)
        }
    }

    fun changePasswordConfirmVisible() {
        viewModelScope.launch {
            _isPasswordConfirmVisible.emit(!_isPasswordConfirmVisible.value)
        }
    }

    fun setIdText(idText: String) {
        viewModelScope.launch {
            _idText.emit(idText)
        }
    }

    fun setPwText(pwText: String) {
        viewModelScope.launch {
            _pwText.emit(pwText)
        }
    }

    fun setPwConfirmText(pwConfirmText: String) {
        viewModelScope.launch {
            _pwConfirmText.emit(pwConfirmText)
        }
    }

    fun setNickNameText(nickNameText: String) {
        viewModelScope.launch {
            _nickNameText.emit(nickNameText)
        }
    }

    object ConfirmType {
        const val SKIP = 1
        const val COVER = 2
    }
}