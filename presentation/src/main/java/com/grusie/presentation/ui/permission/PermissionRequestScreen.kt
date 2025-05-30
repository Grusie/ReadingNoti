package com.grusie.presentation.ui.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.grusie.presentation.R
import com.grusie.presentation.data.permission.PermissionState

@Composable
fun PermissionRequestScreen(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionState = rememberNotificationPermissionState {
        onPermissionGranted()
    }

    // 최초 진입 시에는 33버전 이상일 때, 알림 권한만 요청
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !permissionState.isGranted
        ) {
            permissionState.launchPermissionRequest()
        }
    }

    // 권한 허용 되어 있을 때에는 해당 뷰가 보여지지 않아야 하기에 둘 중 하나라도 안 되어 있을 경우 처리
    if(!permissionState.isGranted || !isNotificationListenerEnabled(context)) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val scrollState = rememberScrollState()
                val buttonSize = 52.dp
                Column(
                    modifier = Modifier.padding(bottom = buttonSize),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                    Image(
                        // TODO: 앱의 대표 이미지로 변경 할 것
                        painter = painterResource(R.drawable.ic_noti_black),
                        contentDescription = "app_icon",
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = context.getString(R.string.str_permission_screen_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 40.dp)
                            .verticalScroll(scrollState)
                            .padding(vertical = 20.dp)
                    ) {
                        PermissionItem(
                            permissionTitle = context.getString(R.string.str_permission_notification_title),
                            permissionDescription = context.getString(R.string.str_permission_notification_description),
                            permissionIconPainter = painterResource(R.drawable.ic_noti_black)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (!permissionState.isGranted) {
                                permissionState.launchPermissionRequest()
                        } else {
                            if (!isNotificationListenerEnabled(context)) {
                                context.startActivity(
                                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            } else {
                                onPermissionGranted()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary)
                        .height(buttonSize)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = context.getString(R.string.str_next),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionItem(
    permissionTitle: String,
    permissionDescription: String,
    permissionIconPainter: Painter
) {
    Spacer(modifier = Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = permissionIconPainter,
                contentDescription = "ic_permission",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column() {
            Text(
                text = permissionTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = permissionDescription,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

fun isNotificationListenerEnabled(context: Context): Boolean {
    val pkgName = context.packageName
    val enabledListeners = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    ) ?: return false

    return enabledListeners.contains(pkgName)
}

/**
 *  알림 권한 허용 상태(33버전 미만이거나, 알림 권한이 허용 된 경우)
 *  알림 권한 요청(알림 권한이 더 이상 뜨지 않을 때에는 설정화면으로 이동하도록 요청)
 */
@Composable
fun rememberNotificationPermissionState(
    context: Context = LocalContext.current,
    onPermissionGranted: () -> Unit = {}
): PermissionState {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (!result) {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity, Manifest.permission.POST_NOTIFICATIONS
            )
            if (!shouldShowRationale) {
                Toast.makeText(
                    context,
                    context.getString(R.string.str_permission_notification_need),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
        isGranted = result
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isGranted =
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED

                if (isGranted && isNotificationListenerEnabled(context)) {
                    onPermissionGranted()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return PermissionState(
        isGranted = isGranted,
        launchPermissionRequest = {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    )
}

@Preview
@Composable
fun PermissionRequestScreenPreview() {
    PermissionRequestScreen({})
}