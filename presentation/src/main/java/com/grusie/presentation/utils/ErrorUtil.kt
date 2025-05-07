package com.grusie.presentation.utils

import android.content.Context
import com.grusie.domain.data.CustomException
import com.grusie.presentation.R

fun Exception.getErrorMsg(context: Context): String {
    return when (this) {
        CustomException.NetworkError -> {
            context.getString(R.string.common_error_network)
        }

        CustomException.NotFoundOnServer -> {
            context.getString(R.string.common_error_data_not_found)
        }

        else -> this.message ?: ""
    }
}

fun Throwable.getErrorMsg(context: Context): String {
    return when (this) {
        CustomException.NetworkError -> {
            context.getString(R.string.common_error_network)
        }

        CustomException.NotFoundOnServer -> {
            context.getString(R.string.common_error_data_not_found)
        }

        else -> this.message ?: ""
    }
}