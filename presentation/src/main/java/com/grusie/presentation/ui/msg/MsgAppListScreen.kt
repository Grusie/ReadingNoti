package com.grusie.presentation.ui.msg

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonAppIcon
import com.grusie.presentation.ui.common.CommonTitleBar

@Composable
fun MsgAppListScreen(
    navController: NavHostController,
    viewModel: MsgAppListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appList = viewModel.appList.collectAsState().value

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
                                popUpTo(Routes.ADMIN) { inclusive = true }
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
                title = context.getString(R.string.title_msg_app_list),
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn {
                items(appList) {item ->
                    MsgAppListItem(item) { clickItem ->
                        viewModel.setEventState(
                            BaseEventState.Navigate(
                                Routes.MSG_LIST, args = mapOf(
                                    Routes.MsgKeys.EXTRA_APP_ID to clickItem.menuId,
                                    Routes.MsgKeys.EXTRA_APP_NAME to clickItem.displayName
                                )
                            )
                        )
                    }
                }
            }

            when (uiState) {
                BaseUiState.Loading -> {
                    CircleProgressBar()
                }
            }
        }
    }
}

@Composable
fun MsgAppListItem(
    appItem: DomainTotalSettingDto,
    onClick: (DomainTotalSettingDto) -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick(appItem) }
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .defaultMinSize(minHeight = 50.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommonAppIcon(
                imageUrl = appItem.imageUrl,
                isTintUse = appItem.isTintUse
            )
            Spacer(Modifier.width(8.dp))

            Text(text = appItem.displayName)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MsgAppListItemPreView() {
    MsgAppListItem(
        appItem = DomainTotalSettingDto(
            displayName = "카카오톡"
        )
    ) { }
}