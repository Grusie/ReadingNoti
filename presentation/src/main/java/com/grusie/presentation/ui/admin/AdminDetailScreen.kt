package com.grusie.presentation.ui.admin

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.domain.data.DomainUserDto
import com.grusie.presentation.R
import com.grusie.presentation.data.setting.AdminSettingEnum
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog

@Composable
fun AdminDetailScreen(
    navController: NavHostController,
    adminType: String,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val adminTypeEnum = AdminSettingEnum.from(adminType)
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value
    var errorMsg by remember { mutableStateOf("") }
    var isShowErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null) {
                when (eventState) {
                    is AdminEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
                    }

                    is AdminEventState.Toast -> {
                        Toast.makeText(context, eventState.toastMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CommonTitleBar(title = adminTypeEnum.getTitle(context), navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (adminTypeEnum) {
                AdminSettingEnum.MANAGE_ADMIN -> {
                    ManageAdminScreen(viewModel)
                }

                AdminSettingEnum.MANAGE_TOTAL_MENU -> {

                }

                AdminSettingEnum.MANAGE_ADD_APP -> {

                }
            }

            when (uiState) {
                AdminUiState.Loading -> {
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
fun ManageAdminScreen(viewModel: AdminViewModel? = null) {
    val userList = viewModel?.userList?.collectAsState()?.value ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel?.getUserList()
    }

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
            .clickable {
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