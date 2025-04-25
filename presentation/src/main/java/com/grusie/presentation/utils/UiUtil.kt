package com.grusie.presentation.utils

object UiUtil {
    class DebounceClickHelper(private val debounceTime: Long = 1000L) {
        private var lastClickTime = 0L

        fun canClick(): Boolean {
            val now = System.currentTimeMillis()
            return if (now - lastClickTime > debounceTime) {
                lastClickTime = now
                true
            } else {
                false
            }
        }
    }
}