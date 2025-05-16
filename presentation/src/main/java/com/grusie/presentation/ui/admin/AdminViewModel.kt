package com.grusie.presentation.ui.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.usecase.storage.StorageUseCases
import com.grusie.domain.usecase.totalSetting.TotalSettingUseCases
import com.grusie.domain.usecase.user.UserUseCases
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.data.setting.AdminSettingEnum
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto
import com.grusie.presentation.mapper.toDomain
import com.grusie.presentation.mapper.toUi
import com.grusie.presentation.ui.base.BaseViewModel
import com.grusie.presentation.utils.getErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storageUseCases: StorageUseCases,
    private val userUseCases: UserUseCases,
    private val totalSettingUseCases: TotalSettingUseCases,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _userList: MutableStateFlow<List<DomainUserDto>> = MutableStateFlow(emptyList())
    val userList: StateFlow<List<DomainUserDto>> = _userList.asStateFlow()

    private val _totalSettingList: MutableStateFlow<List<UiTotalSettingDto>> = MutableStateFlow(
        emptyList()
    )
    val totalSettingList: StateFlow<List<UiTotalSettingDto>> = _totalSettingList.asStateFlow()

    val adminTypeEnum = savedStateHandle.get<String>(Routes.AdminKeys.EXTRA_ADMIN_TYPE)
        ?.let { AdminSettingEnum.from(it) }
    val initDetailTotalSettingDto = savedStateHandle.get<String>(Routes.Keys.EXTRA_DATA)
        ?.let { Json.decodeFromString<UiTotalSettingDto>(it) }

    private val _detailTotalSettingDto: MutableStateFlow<UiTotalSettingDto?> =
        MutableStateFlow(initDetailTotalSettingDto)
    val detailTotalSettingDto: StateFlow<UiTotalSettingDto?> = _detailTotalSettingDto.asStateFlow()

    init {
        if (adminTypeEnum != null) {
            when (adminTypeEnum) {
                AdminSettingEnum.MANAGE_ADMIN -> {
                    getUserList()
                }

                else -> {}
            }
        }
    }

    /**
     * 유저 리스트를 로드
     * 관리자, 일반 유저 순서로 정렬하여 리스트를 뿌려줌
     */
    private fun getUserList() {
        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            userUseCases.getUserListUseCase().onSuccess { list ->
                _userList.value = list.sortedWith(
                    compareByDescending<DomainUserDto> { it.isAdmin }.thenBy { it.name }
                )
            }.onFailure { e ->
                setEventState(AdminEventState.Error(errorMsg = e.getErrorMsg(context)))
            }
            setUiState(AdminUiState.Idle)
        }
    }

    fun getTotalSettingList(type: SettingType) {
        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            totalSettingUseCases.getServerTotalSettingListUseCase(type).onSuccess { list ->
                _totalSettingList.value = list.map { it.toUi() }
            }.onFailure { e ->
                setEventState(AdminEventState.Error(errorMsg = e.getErrorMsg(context)))
            }
            setUiState(AdminUiState.Idle)
        }
    }

    /**
     * 특정 메뉴의 보여짐 여부를 처리 -> 보여짐 여부를 false로 할 경우, inInitEnabled도 자동으로 false가 된다.
     *
     * @param menuId
     * @param visibility
     */
    fun updateTotalSettingVisibility(menuId: Int, visibility: Boolean) {
        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            totalSettingUseCases.updateTotalSettingVisibilityUseCase(menuId, visibility).onSuccess {

            }.onFailure { e ->
                setEventState(AdminEventState.Error(e.getErrorMsg(context)))
            }
            setUiState(AdminUiState.Idle)
        }
    }

    /**
     * 특정 메뉴의 세팅정보를 저장한다
     */
    fun setTotalSettingChanged() {
        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            _detailTotalSettingDto.value?.let {
                totalSettingUseCases.setTotalSettingUseCase(
                    initDetailTotalSettingDto?.toDomain(),
                    it.toDomain()
                )
                    .onSuccess {
                        setEventState(AdminEventState.Success(SuccessType.SUCCESS_MODIFY))
                    }.onFailure { e ->
                        setEventState(AdminEventState.Error(e.getErrorMsg(context)))
                    }
            } ?: setEventState(AdminEventState.Error(Exception().getErrorMsg(context)))
            setUiState(AdminUiState.Idle)
        }
    }

    /**
     * 특정 사용자를 관리자로 만들거나 관리자 권한을 해제
     *
     * @param uid 관리자 처리를 위한 대상
     * @param isAdmin 관리자 권한 부여 / 해제
     */
    fun setAdmin(uid: String, isAdmin: Boolean) {
        viewModelScope.launch {
            setUiState(AdminUiState.Loading)

            if (uid == auth.currentUser?.uid && !isAdmin) {
                setEventState(AdminEventState.Error(errorMsg = context.getString(R.string.manage_admin_remove_me_error)))
            } else {
                userUseCases.setAdminUseCase(uid, isAdmin).onSuccess {
                    getUserList()
                }.onFailure { e ->
                    setEventState(AdminEventState.Error(errorMsg = e.getErrorMsg(context)))
                }
            }
            setUiState(AdminUiState.Idle)
        }
    }

    /**
     * 이미지를 스토리지에 업로드
     * 앱 아이콘을 수동으로 올릴 때 사용
     *
     * @param path 스토리지 최하단 파일 이름
     * @param uri 이미지 경로
     */
    fun uploadImageToStorage(path: String, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return

        viewModelScope.launch {
            setUiState(AdminUiState.Loading)
            storageUseCases.uploadFileToStorageUseCase(path = path, bytes = bytes).onSuccess {
                setEventState(AdminEventState.Error(errorMsg = "성공"))
            }.onFailure { e ->
                setEventState(AdminEventState.Error(errorMsg = e.getErrorMsg(context)))
            }
            setUiState(AdminUiState.Idle)
        }
    }

    /**
     * 현재 수정한 설정 정보를 화면에 표시하기 위해 뷰모델에만 적용
     */
    fun setDetailTotalSettingDto(detailTotalSettingDto: UiTotalSettingDto) {
        viewModelScope.launch {
            _detailTotalSettingDto.emit(detailTotalSettingDto)
        }
    }

    object ConfirmType {
        const val CONFIRM = 1
        const val CANCEL = 2
    }

    object SuccessType {
        const val SUCCESS_MODIFY = 100
    }
}