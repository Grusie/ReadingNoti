package com.grusie.core.common

object ServerKey {
    object TotalSetting {
        const val KEY_VISIBLE = "isVisible"
        const val KEY_INIT_ENABLED = "isInitEnabled"
        const val KEY_TYPE = "type"
        const val KEY_MENU_ID = "menuId"
        const val KEY_DISPLAY_NAME = "displayName"
        const val KEY_DESCRIPTION = "description"

        object APP {
            const val KEY_PACKAGE = "package"
            const val KEY_IMAGE_URL = "imageUrl"
            const val KEY_IS_TINT_USE = "isTintUse"
        }
    }

    object PersonalSetting {
        const val KEY_MENU_ID = "menuId"
        const val KEY_ENABLED = "isEnabled"
        const val KEY_CUSTOM_DATA = "customData"
        const val KEY_TYPE = "type"
    }

    object User {
        const val KEY_UID = "uid"
        const val KEY_EMAIL = "email"
        const val KEY_IS_ADMIN = "isAdmin"
        const val KEY_NAME = "name"
    }
}