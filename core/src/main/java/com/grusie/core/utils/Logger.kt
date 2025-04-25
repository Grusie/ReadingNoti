package com.grusie.core.utils

import android.util.Log
import com.grusie.core.BuildConfig

/**
 * 로그를 찍는 Utils
 * BaseActivity, BaseViewModel 등에서 미리 구현해서 사용
 */
object Logger {
    private val isDebug = BuildConfig.DEBUG

    enum class LogType {
        LOG_TYPE_D,
        LOG_TYPE_I,
        LOG_TYPE_E
    }

    fun log(logType: LogType, tag: String, message: String) {
        when (logType) {
            LogType.LOG_TYPE_E -> {
                e(tag, message)
            }

            LogType.LOG_TYPE_D -> {
                d(tag, message)
            }

            LogType.LOG_TYPE_I -> {
                i(tag, message)
            }
        }
    }

    fun logException(tag: String, exception: Exception) {
        e(tag, exception)
    }

    fun d(tag: String, message: String) {
        if (isDebug) Log.d("LOG TAG : $tag", "LOG MSG : $message")
    }

    fun i(tag: String, message: String) {
        if (isDebug) Log.i("LOG TAG : $tag", "LOG MSG : $message")
    }

    fun e(tag: String, message: String) {
        if (isDebug) Log.e("LOG TAG : $tag", "LOG MSG : $message")
    }

    fun e(tag: String, exception: Exception) {
        if (isDebug) Log.e("LOG TAG : $tag", "LOG MSG : ${exception.message}")
    }
}