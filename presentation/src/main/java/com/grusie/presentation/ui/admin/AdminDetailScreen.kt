package com.grusie.presentation.ui.admin

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainUserDto
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.data.setting.AdminSettingEnum
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog
import com.grusie.presentation.ui.common.TitleButtonItem
import com.grusie.presentation.ui.common.debounceClickable
import kotlinx.serialization.json.Json

@Composable
fun AdminDetailScreen(
    navController: NavHostController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value
    var errorMsg by remember { mutableStateOf("") }
    var isShowErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null) {
                when (eventState) {
                    is BaseEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
                    }

                    is BaseEventState.Toast -> {
                        Toast.makeText(context, eventState.toastMsg, Toast.LENGTH_SHORT).show()
                    }

                    is BaseEventState.Navigate -> {
                        val fullRoute = buildString {
                            append(eventState.route)
                            if (eventState.args.isNotEmpty()) {
                                append("?")
                                append(eventState.args.entries.joinToString("&") { "${it.key}=${it.value}" })
                            }
                        }
                        navController.navigate(fullRoute) {
                            if (eventState.includeBackStack) {
                                popUpTo(Routes.DETAIL_ADMIN) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CommonTitleBar(
                title = viewModel.adminTypeEnum?.getTitle(context) ?: "",
                navController = navController,
                rightButton =
                if (viewModel.adminTypeEnum == AdminSettingEnum.MANAGE_ADD_APP) listOf(
                    TitleButtonItem(iconRes = R.drawable.ic_add, onClick = {
                        viewModel.setEventState(
                            BaseEventState.Navigate(
                                Routes.DETAIL_ADMIN_MODIFY, args = mutableMapOf(
                                    Routes.Keys.EXTRA_DATA to ""
                                )
                            )
                        )
                    })
                ) else null
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (viewModel.adminTypeEnum == null) return@Scaffold
            when (viewModel.adminTypeEnum) {
                AdminSettingEnum.MANAGE_ADMIN -> {
                    ManageAdminScreen(viewModel)
                }

                AdminSettingEnum.MANAGE_TOTAL_MENU -> {
                    ManageTotalSettingScreen(viewModel)
                }

                AdminSettingEnum.MANAGE_ADD_APP -> {
                    ManageAppScreen(viewModel)
                }
            }

            when (uiState) {
                BaseUiState.Loading -> {
                    CircleProgressBar()
                }
            }

            OneButtonAlertDialog(
                isShowDialog = isShowErrorDialog,
                onClickConfirm = { isShowErrorDialog = false },
                title = context.getString(R.string.common_error_title_notice_msg),
                content = errorMsg,
            )
        }
    }
}

@Composable
fun ManageTotalSettingScreen(viewModel: AdminViewModel? = null) {
    val totalSettingList = viewModel?.totalSettingList?.collectAsState()?.value ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel?.getTotalSettingList(SettingType.GENERAL)
    }

    LazyColumn() {
        items(totalSettingList) {
            TotalSettingListItem(it) { totalSetting ->
                viewModel?.setEventState(
                    BaseEventState.Navigate(
                        Routes.DETAIL_ADMIN_MODIFY, args = mutableMapOf(
                            Routes.Keys.EXTRA_DATA to Uri.encode(Json.encodeToString(totalSetting))
                        )
                    )
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),

                thickness = 1.dp,
            )
        }
    }
}

@Composable
fun ManageAppScreen(viewModel: AdminViewModel? = null) {
    val appList = viewModel?.totalSettingList?.collectAsState()?.value ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel?.getTotalSettingList(SettingType.APP)
    }

    LazyColumn() {
        items(appList) {
            AppListItem(it) { appItem ->
                viewModel?.setEventState(
                    BaseEventState.Navigate(
                        Routes.DETAIL_ADMIN_MODIFY, args = mutableMapOf(
                            Routes.Keys.EXTRA_DATA to Uri.encode(Json.encodeToString(appItem))
                        )
                    )
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),

                thickness = 1.dp,
            )
        }
    }
}

@Composable
fun ManageAdminScreen(viewModel: AdminViewModel? = null) {
    val userList = viewModel?.userList?.collectAsState()?.value ?: emptyList()

//    LaunchedEffect(Unit) {
//        viewModel?.getUserList()
//    }

    LazyColumn() {
        var isFirstNormalUser = false
        items(userList) {
            if (!isFirstNormalUser) {
                if (!it.isAdmin) {
                    isFirstNormalUser = true
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),

                        thickness = 1.dp,
                    )
                }
            }
            UserListItem(it, setAdmin = { uid, isAdmin -> viewModel?.setAdmin(uid, isAdmin) })

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),

                thickness = 1.dp,
            )
        }
    }
}


@Composable
fun UserListItem(
    domainUserDto: DomainUserDto,
    setAdmin: (String, Boolean) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = 60.dp)
            .fillMaxWidth()
            .debounceClickable {
                setAdmin(domainUserDto.uid, !domainUserDto.isAdmin)
            }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Checkbox(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically),
                checked = domainUserDto.isAdmin,
                onCheckedChange = { setAdmin(domainUserDto.uid, !domainUserDto.isAdmin) }
            )
            Spacer(Modifier.width(8.dp))

            Column(
                Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(
                    text = "${domainUserDto.name}(${domainUserDto.email.ifEmpty { domainUserDto.uid }})",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TotalSettingListItem(
    totalSettingDto: UiTotalSettingDto,
    navigateToModifyScreen: (UiTotalSettingDto) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .debounceClickable {
                navigateToModifyScreen(totalSettingDto)
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${totalSettingDto.totalAppSettingEnum?.name}(${totalSettingDto.menuId})",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = totalSettingDto.displayName,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis, maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = if (totalSettingDto.isVisible) "ON" else "OFF",
            color = if (totalSettingDto.isVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp
        )
    }
}

@Composable
fun AppListItem(
    appItem: UiTotalSettingDto,
    navigateToModifyScreen: (UiTotalSettingDto) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .debounceClickable {
                navigateToModifyScreen(appItem)
            }
            .padding(start = 20.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp),
            painter = if (LocalInspectionMode.current) {
                painterResource(R.drawable.ic_image_placeholder)
            } else {
                rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(appItem.imageUrl)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .build()
                )
            },
            contentDescription = "app_icon",
            colorFilter = if (appItem.isTintUse) ColorFilter.tint(MaterialTheme.colorScheme.onBackground) else null
        )

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${appItem.packageName}(${appItem.menuId})",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = appItem.displayName,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis, maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = if (appItem.isVisible) "ON" else "OFF",
            color = if (appItem.isVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview(showBackground = true)
fun UserListItemPreview() {
    UserListItem(
        DomainUserDto(
            uid = "uid 입니다.",
            email = "email 입니다.",
            name = "name 입니다.",
            isAdmin = true
        )
    )
}


@Composable
@Preview(showBackground = true)
fun TotalSettingListItemPreview() {
    val totalSettingDto = UiTotalSettingDto(
        isVisible = true,
        isInitEnabled = true,
        description = "테스트 세팅 설명입니다. 2줄까지 가능하기에 길게 한 번 넣어보도록 하죠 이게 과연 중앙이 맞는지 의심되는군요 중앙정렬 치고는 위로 좀 올라와 있는 거 같은데... 어이없네요",
        displayName = "얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네, 얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네",
        totalAppSettingEnum = TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED
    )
    TotalSettingListItem(
        totalSettingDto
    )
}

@Composable
@Preview(showBackground = true)
fun AppListItemPreview() {
    val totalSettingDto = UiTotalSettingDto(
        isVisible = true,
        isInitEnabled = true,
        description = "테스트 세팅 설명입니다. 2줄까지 가능하기에 길게 한 번 넣어보도록 하죠 이게 과연 중앙이 맞는지 의심되는군요 중앙정렬 치고는 위로 좀 올라와 있는 거 같은데... 어이없네요",
        displayName = "얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네, 얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네",
        totalAppSettingEnum = TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED,
        imageUrl = "imageUrl",
        packageName = "com.grusie.readingnoti"
    )
    AppListItem(
        totalSettingDto
    )
}