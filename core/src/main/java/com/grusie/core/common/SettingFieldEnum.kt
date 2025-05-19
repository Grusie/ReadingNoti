package com.grusie.core.common

enum class SettingFieldEnum(
    val type: Int,
    val title: String,
    val description: String,
    val fieldName: String,   // 서버의 field명과 동일 시 할 것
    val isEssential: Boolean
) {
    MENU_ID(FieldType.STRING_TYPE,"메뉴 아이디", "메뉴 고유의 아이디로, 임의로 설정 할 수 없습니다.", ServerKey.TotalSetting.KEY_MENU_ID, true),
    VISIBLE(FieldType.BOOLEAN_TYPE,"메뉴 보여짐 여부", "메뉴의 보여짐 여부를 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_VISIBLE, true),
    ENABLED(FieldType.BOOLEAN_TYPE,"사용 여부", "메뉴 기본 사용 여부를 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_INIT_ENABLED, true),
    DISPLAY_NAME(FieldType.STRING_TYPE,"메뉴 타이틀", "메뉴 명을 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_DISPLAY_NAME, true),
    DESCRIPTION(FieldType.STRING_TYPE,"메뉴 설명", "메뉴 설명을 설정 할 수 있습니다.", ServerKey.TotalSetting.KEY_DESCRIPTION, false),
    PACKAGE(FieldType.STRING_TYPE,"패키지 명", "패키지 명을 수정할 수 있습니다.", ServerKey.TotalSetting.APP.KEY_PACKAGE, true),
    IMAGE_URL(FieldType.FILE_TYPE,"아이콘 경로", "아이콘을 수정할 수 있습니다.",ServerKey.TotalSetting.APP.KEY_IMAGE_URL, false),
    ICON_TINT(FieldType.CHECK_TYPE, "아이콘 틴트 사용", "아이콘 틴트를 사용할지에 관한 여부를 설정 할 수 있습니다.\n배경이 투명할 경우에만 사용하시길 바랍니다.", ServerKey.TotalSetting.APP.KEY_IS_TINT_USE, false);

    companion object {
        fun getGeneralField() = listOf(MENU_ID, VISIBLE, ENABLED, DISPLAY_NAME, DESCRIPTION)
        fun getAppField() = listOf(MENU_ID, VISIBLE, ENABLED, DISPLAY_NAME, PACKAGE, IMAGE_URL)
    }

    object FieldType {
        const val BOOLEAN_TYPE = 0
        const val STRING_TYPE = 1
        const val FILE_TYPE = 2
        const val CHECK_TYPE = 3
    }
}