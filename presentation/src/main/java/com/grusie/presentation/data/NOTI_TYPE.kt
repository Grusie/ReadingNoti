package com.grusie.presentation.data

/**
 * 알림을 받아들일 어플리케이션들을 정의 하는 Enum Class
 *
 * @param notiTypeId 각 앱들의 notiTypeId 값
 */

enum class NOTI_TYPE(val notiTypeId:Int) {
    NONE(10000),
    KAKAO(10001),
    DOUZONE(10002);

    companion object {
        fun getNotiType(notiTypeId: Int): NOTI_TYPE {
            return entries.firstOrNull { it.notiTypeId == notiTypeId } ?: NONE
        }
    }
}