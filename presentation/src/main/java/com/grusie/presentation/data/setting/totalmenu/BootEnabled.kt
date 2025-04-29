package com.grusie.presentation.data.setting.totalmenu

import com.grusie.presentation.R
import com.grusie.presentation.data.setting.BaseSettingMenu

/**
 * 휴대폰을 부팅할 때 실행할것인지에 대한 처리를 진행
 */
class BootEnabled: BaseSettingMenu() {
    override val drawableResId: Int
        get() = R.drawable.ic_reboot_black
}