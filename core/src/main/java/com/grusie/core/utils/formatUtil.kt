package com.grusie.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toTimeString(format: String = "yy.MM.dd (E) HH:mm"): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat(format, Locale.KOREAN)
    return dateFormat.format(date)
}