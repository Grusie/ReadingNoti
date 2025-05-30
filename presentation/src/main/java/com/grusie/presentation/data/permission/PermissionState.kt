package com.grusie.presentation.data.permission

data class PermissionState(
    val isGranted: Boolean,
    val launchPermissionRequest: () -> Unit
)