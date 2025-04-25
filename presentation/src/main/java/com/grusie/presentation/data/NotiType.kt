package com.grusie.presentation.data

data class NotiType(
    val id: Int = -1,   // 설정할 앱 고유 아이디
    val displayName: String = "",   // 화면에 보여줄 이름
    val packageName: String = "",   // 패키지 명
    val icon: String = "",   // 앱 아이콘
    val isEnabled: Boolean = true
)
