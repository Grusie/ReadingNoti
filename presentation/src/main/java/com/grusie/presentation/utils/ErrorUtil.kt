package com.grusie.presentation.utils

import android.content.Context
import com.grusie.domain.data.AuthException
import com.grusie.domain.data.CommonException
import com.grusie.presentation.R

fun Exception.getErrorMsg(context: Context): String {
    return when (this) {
        is CommonException.NetworkError -> {
            context.getString(R.string.common_error_network)
        }

        is CommonException.NotFoundOnServer -> {
            context.getString(R.string.common_error_data_not_found)
        }

        is CommonException.DataMatchingError -> {
            context.getString(R.string.common_error_data_not_matched)
        }

        is CommonException.EssentialError -> {
            context.getString(R.string.common_error_essential_data_empty)
        }

        is AuthException.EmailPwIncorrectError -> {
            context.getString(R.string.auth_error_email_pw_incorrect)
        }

        is AuthException.PwConfirmIncorrectError -> {
            context.getString(R.string.auth_error_pw_confirm_incorrect)
        }

        is AuthException.EmailTypeMatchingError -> {
            context.getString(R.string.auth_error_not_email_format)
        }

        is AuthException.PwLengthError -> {
            context.getString(R.string.auth_error_pw_length, AuthException.EXTRA_PW_MIN_LENGTH)
        }

        is AuthException.DuplicationEmailError -> {
            context.getString(R.string.auth_error_duplication_email)
        }

        else -> this.message ?: context.getString(R.string.common_error_unknown_msg)
    }
}

fun Throwable.getErrorMsg(context: Context): String {
    return when (this) {
        is CommonException.NetworkError -> {
            context.getString(R.string.common_error_network)
        }

        is CommonException.NotFoundOnServer -> {
            context.getString(R.string.common_error_data_not_found)
        }

        is CommonException.DataMatchingError -> {
            context.getString(R.string.common_error_data_not_matched)
        }

        else -> this.message ?: context.getString(R.string.common_error_unknown_msg)
    }
}