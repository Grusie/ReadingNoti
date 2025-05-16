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

        else -> this.message ?: context.getString(R.string.common_error_unknown_msg)
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

        CustomException.DataMatchingError -> {
            context.getString(R.string.common_error_data_not_matched)
        }

        else -> this.message ?: context.getString(R.string.common_error_unknown_msg)
    }
}