package com.grusie.presentation.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun OneButtonAlertDialog(
    isShowDialog: Boolean,
    onClickConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    title: String,
    content: String,
    confirmText: String? = null,
) {
    if (isShowDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss?.invoke() ?: onClickConfirm() },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { onClickConfirm() }) {
                    Text(confirmText ?: "확인")
                }
            }
        )
    }
}

@Composable
fun TwoButtonAlertDialog(
    isShowDialog: Boolean,
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit = { },
    onDismiss: (() -> Unit)? = null,
    title: String,
    content: String,
    confirmText: String? = null,
    cancelText: String? = null
) {
    if (isShowDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss?.invoke() ?: onClickCancel() },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { onClickConfirm() }) {
                    Text(confirmText ?: "확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { onClickCancel() }) {
                    Text(cancelText ?: "취소")
                }
            }
        )
    }
}