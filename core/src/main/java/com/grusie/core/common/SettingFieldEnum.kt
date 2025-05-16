package com.grusie.core.common

enum class SettingFieldEnum(
    val type: Int,
    val title: String,
    val description: String,
    val fieldName: String   // 서버의 field명과 동일 시 할 것
) {
    VISIBLE(FieldType.BOOLEAN_TYPE,"메뉴 보여짐 여부", "메뉴의 보여짐 여부를 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_VISIBLE),
    ENABLED(FieldType.BOOLEAN_TYPE,"사용 여부", "메뉴 기본 사용 여부를 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_INIT_ENABLED),
    DISPLAY_NAME(FieldType.STRING_TYPE,"메뉴 타이틀", "메뉴 명을 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_DISPLAY_NAME),
    DESCRIPTION(FieldType.STRING_TYPE,"메뉴 설명", "메뉴 설명을 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_DESCRIPTION),
    PACKAGE(FieldType.STRING_TYPE,"패키지 명", "패키지 명을 수정할 수 있습니다.", ServerKey.TotalSetting.APP.KEY_PACKAGE),
    IMAGE_URL(FieldType.FILE_TYPE,"아이콘 경로", "아이콘을 수정할 수 있습니다.",ServerKey.TotalSetting.APP.KEY_IMAGE_URL);

    companion object {
        fun getGeneralField() = listOf(VISIBLE, ENABLED, DISPLAY_NAME, DESCRIPTION)
        fun getAppField() = listOf(VISIBLE, ENABLED, DISPLAY_NAME, PACKAGE, IMAGE_URL)
    }

    object FieldType {
        const val BOOLEAN_TYPE = 0
        const val STRING_TYPE = 1
        const val FILE_TYPE = 2
    }
}