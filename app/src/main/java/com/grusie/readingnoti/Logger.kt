package com.grusie.readingnoti

import android.util.Log
import com.grusie.core.utils.LogType
import com.grusie.core.utils.LoggerInterface

class Logger(
    private val isDebug: Boolean
) : LoggerInterface {
    override fun log(logType: LogType, tag: String, message: String) {
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

    override fun logException(tag: String, exception: Exception) {
        e(tag, exception)
    }

    override fun d(tag: String, message: String) {
        if (isDebug) Log.d(tag, "LOG MSG : $message")
    }

    override fun i(tag: String, message: String) {
        if (isDebug) Log.i(tag, "LOG MSG : $message")
    }

    override fun e(tag: String, message: String) {
        if (isDebug) Log.e(tag, "LOG MSG : $message")
    }

    override fun e(tag: String, exception: Exception) {
        if (isDebug) Log.e(tag, "LOG MSG : ${exception.message}")
    }
}