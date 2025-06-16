package com.grusie.presentation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MAIN = "main"
    const val SETTING = "setting"
    const val ADMIN = "admin"
    const val DETAIL_ADMIN = "detail_admin"
    const val DETAIL_ADMIN_MODIFY = "detail_admin_modify"
    const val SIGNUP = "signup"
    const val PERMISSION = "permission"
    const val MSG_APP_LIST = "msg_app_list"
    const val MSG_LIST = "msg_list"

    object Keys {
        const val EXTRA_DATA = "extra_data"
    }

    object AdminKeys {
        const val EXTRA_ADMIN_TYPE = "extra_admin_type"
    }

    object PermissionKeys {
        const val EXTRA_AUTH = "extra_auth"
    }

    object MsgKeys {
        const val EXTRA_APP_ID = "extra_app_id"
        const val EXTRA_APP_NAME = "extra_app_name"
    }
}