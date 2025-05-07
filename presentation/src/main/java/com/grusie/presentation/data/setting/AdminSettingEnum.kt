package com.grusie.presentation.data.setting

import android.content.Context
import androidx.annotation.StringRes
import com.grusie.presentation.R

enum class AdminSettingEnum(
    @StringRes val titleResId: Int,
    @StringRes val contentResId: Int
) {
    MANAGE_ADMIN(R.string.manage_admin_title, R.string.manage_admin_content),
    MANAGE_TOTAL_MENU(R.string.manage_total_setting_title, R.string.manage_total_setting_content),
    MANAGE_ADD_APP(R.string.manage_app_setting_title, R.string.manage_app_setting_content);

    fun getTitle(context: Context): String = context.getString(titleResId)
    fun getContent(context: Context): String = context.getString(contentResId)

    companion object {
        fun from(type: String): AdminSettingEnum = AdminSettingEnum.valueOf(type)
    }
}