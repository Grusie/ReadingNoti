package com.grusie.core.common

/**
 * 설정들의 타입을 나타내는 Enum 클래스
 */
enum class SettingType {
    GENERAL, // 전체 앱 설정 타입
    APP;          // 앱 설정 타입 ex) 카카오톡, 페이스북 등

    companion object {
        fun from(value: String): SettingType {
            return entries.firstOrNull { it.name == value } ?: GENERAL
        }
    }
}