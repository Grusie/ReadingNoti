package com.grusie.presentation.ui.msg

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.core.utils.toTimeString
import com.grusie.domain.data.DomainMsgData
import com.grusie.presentation.R
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonMsgDetail
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.TitleButtonItem
import com.grusie.presentation.ui.common.TitleIcon
import com.grusie.presentation.ui.common.TwoButtonAlertDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MsgListScreen(
    navController: NavHostController,
    viewModel: MsgListViewModel = hiltViewModel()
) {
    val msgList = viewModel.msgList.collectAsState().value
    val expandedIds = viewModel.expandedIds.collectAsState().value
    var isConfirmDialogVisible by remember { mutableStateOf(false) }
    var confirmType by remember { mutableStateOf(0) }
    var isMsgDetailVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.eventState.collectLatest {eventState ->
            when(eventState) {
                is MsgEventState.MsgConfirm -> {
                    isMsgDetailVisible = false
                    isConfirmDialogVisible = true
                    confirmType = eventState.type
                }
            }
        }
    }

    Scaffold(topBar = {
        CommonTitleBar(
            title = viewModel.appDisplayName,
            navController = navController,
            rightButton = listOf(
                TitleButtonItem(
                    titleIcon = TitleIcon.DrawableIcon(R.drawable.ic_delete),
                    onClick = {
                        viewModel.setEventState(MsgEventState.MsgConfirm(MsgListViewModel.ConfirmType.DELETE))
                    }
                )
            )
        )
    }) { paddingValues ->
        var msgDetail: DomainMsgData? by remember { mutableStateOf(null) }

        BackHandler {
            if (isMsgDetailVisible) {
                isMsgDetailVisible = false
            } else {
                navController.popBackStack()
            }
        }

        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState == BaseUiState.Loading -> {
                    CircleProgressBar()
                }

                msgList.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(60.dp),
                            imageVector = Icons.Default.Info,
                            contentDescription = "info_icon",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = context.getString(R.string.str_empty_msg),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        item { Spacer(Modifier.padding(8.dp)) }
                        items(msgList) { msgItem ->
                            MsgListItem(
                                msgItem = msgItem,
                                onClick = {
                                    msgDetail = it
                                    isMsgDetailVisible = true
                                },
                                onExpend = { viewModel.toggleExpand(it) },
                                isExpend = msgItem.id?.let { expandedIds.contains(it) } ?: false
                            )
                        }
                    }
                }
            }

            if (isMsgDetailVisible) {
                msgDetail?.let {
                    CommonMsgDetail(it) {
                        isMsgDetailVisible = false
                    }
                }
            }

            TwoButtonAlertDialog(
                isShowDialog = isConfirmDialogVisible,
                onClickConfirm = {
                    when (confirmType) {
                        MsgListViewModel.ConfirmType.DELETE -> {
                            viewModel.deleteAllList()
                        }

                        else -> {
                            navController.popBackStack()
                        }
                    }
                    isConfirmDialogVisible = false
                },
                onClickCancel = { isConfirmDialogVisible = false },
                title = context.getString(R.string.common_error_title_notice_msg),
                content = when (confirmType) {
                    MsgListViewModel.ConfirmType.DELETE -> context.getString(R.string.str_delete)
                    else -> context.getString(R.string.str_delete)
                }
            )
        }
    }
}

@Composable
fun MsgListItem(
    msgItem: DomainMsgData,
    onClick: (DomainMsgData) -> Unit = {},
    isExpend: Boolean = false,
    onExpend: (Long) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .background(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable { onClick(msgItem) }
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row() {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                if (msgItem.subTitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = msgItem.subTitle,
                        maxLines = 1,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (msgItem.title.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(if (msgItem.subTitle.isNotEmpty()) 2.dp else 4.dp))
                    Text(
                        text = msgItem.title,
                        maxLines = 1,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = msgItem.content,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start,
                    maxLines = if (isExpend) Int.MAX_VALUE else 2
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = msgItem.timeStamp.toTimeString(),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp
                )
            }

            IconButton(
                modifier = Modifier.size(40.dp),
                onClick = {
                    msgItem.id?.let { onExpend(it) }
                }
            ) {
                Icon(
                    imageVector = if (isExpend) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "msg_expend"
                )
            }
        }
    }
}

@Composable
@Preview
fun MsgListItemPreview() {
    MsgListItem(
        DomainMsgData(
            menuId = -1,
            title = "보낸 사람",
            subTitle = "채팅방 이름",
            content = "내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용",
            timeStamp = System.currentTimeMillis()
        )
    ) {}
}