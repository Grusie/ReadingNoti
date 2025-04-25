package com.grusie.presentation.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.presentation.Routes
import com.grusie.presentation.service.NotificationReceiverService

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        var hasPermission by remember {
            mutableStateOf(isNotificationListenerEnabled(context))
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Text("알림 권한 상태: ${if (hasPermission) "허용됨" else "미허용"}")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                }) {
                    Text("알림 접근 권한 설정하기")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (hasPermission) {
                        rebindNotificationListenerService(context)
                        Toast.makeText(context, "서비스 재바인딩 시도함", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "먼저 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("서비스 강제 재등록")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate(Routes.SETTING) }) {
                    Text("Go to Setting")
                }
            }
        }
    }
}


fun rebindNotificationListenerService(context: Context) {
    val cn = ComponentName(context, NotificationReceiverService::class.java)
    val pm = context.packageManager

    pm.setComponentEnabledSetting(
        cn,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
    pm.setComponentEnabledSetting(
        cn,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
}

fun isNotificationListenerEnabled(context: Context): Boolean {
    val enabledListeners = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    ) ?: return false
    return enabledListeners.contains(context.packageName)
}