package com.grusie.core.common

import kotlinx.serialization.Serializable

/**
 * 전체 설정 내부 값들을 나타내는 Enum 클래스
 * Enum이름을 docName과 동일하게 할 것
 */
@Serializable
enum class TotalMenu(val menuId: Int) {
    COLLECT_NOTI_ENABLED(MenuId.COLLECT_NOTI_ENABLED), // 알림 수집
    FOCUS_MODE(MenuId.FOCUS_MODE),    // 방해금지
    BOOT_ENABLED(MenuId.BOOT_ENABLED), // 부팅 시 자동 실행
    TTS_ENABLED(MenuId.TTS_ENABLED);    // TTS 실행 여부

    companion object {
        fun from(menuId: Int): TotalMenu? {
            return entries.firstOrNull { it.menuId == menuId }
        }

        object MenuId {
            const val COLLECT_NOTI_ENABLED = 100
            const val TTS_ENABLED = 101
            const val FOCUS_MODE = 102
            const val BOOT_ENABLED = 103
        }
    }
}