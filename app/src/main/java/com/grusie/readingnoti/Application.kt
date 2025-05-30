package com.grusie.readingnoti

import android.app.Application
import com.google.firebase.FirebaseApp
import com.grusie.core.common.TotalMenu
import com.grusie.core.utils.LoggerProvider
import com.grusie.readingnoti.di.AppEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltAndroidApp
class Application : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        LoggerProvider.logger = Logger(isDebug = BuildConfig.DEBUG)
        FirebaseApp.initializeApp(this)

        initSettingObserver()
        observeTotalSetting()
    }

    private fun initSettingObserver() {
        val entryPoint = EntryPointAccessors.fromApplication(this, AppEntryPoint::class.java)
        val useCases = entryPoint.totalSettingUseCases()

        SettingObserveManager.init(applicationScope, useCases)
    }

    private fun observeTotalSetting() {
        val entryPoint = EntryPointAccessors.fromApplication(this, AppEntryPoint::class.java)
        val ttsServiceController = entryPoint.ttsServiceController()

        applicationScope.launch {
            SettingObserveManager.mergedSettingMap
                .map { mergedMap ->
                    mergedMap[TotalMenu.TOTAL_NOTI_ENABLED.menuId]?.personalSetting?.isEnabled == true
                }
                .distinctUntilChanged()
                .collect { isEnabled ->
                    if(isEnabled) {
                        ttsServiceController.startService()
                    } else {
                        ttsServiceController.stopService()
                    }
                }
        }
    }
}