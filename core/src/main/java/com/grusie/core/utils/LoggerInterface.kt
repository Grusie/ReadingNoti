package com.grusie.core.utils

/**
 * 로그를 찍는 Utils
 * BaseActivity, BaseViewModel 등에서 미리 구현해서 사용
 */
interface LoggerInterface {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun e(tag: String, message: String)
    fun e(tag: String, exception: Exception)
    fun log(logType: LogType, tag: String, message: String)
    fun logException(tag: String, exception: Exception)
}

object LoggerProvider {
    lateinit var logger: LoggerInterface
}