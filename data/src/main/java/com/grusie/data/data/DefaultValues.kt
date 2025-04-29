package com.grusie.data.data

object DefaultValues {

    // 기본 전체 설정 리스트(최초 진입 시에만 사용되는 리스트 <- 서버와 1:1로 대응해야 한다)
    val initLocalTotalSettingList: List<LocalTotalSettingEntity> = listOf(
        LocalTotalSettingEntity(
            description = "전체 알림 읽기를 키거나, 끌 수 있습니다.",
            displayName = "알림 읽기",
            isInitEnabled = true,
            isVisible = true,
            menuId = 100
        ),
        LocalTotalSettingEntity(
            description = "방해금지 시간을 설정 할 수 있습니다.",
            displayName = "방해금지설정",
            isInitEnabled = false,
            isVisible = true,
            menuId = 101
        ),
        LocalTotalSettingEntity(
            description = "휴대폰을 재부팅 했을 때 리딩알리미를 자동으로 실행 할 것인지를 설정 할 수 있습니다.",
            displayName = "재부팅 시 자동 실행",
            isInitEnabled = true,
            isVisible = true,
            menuId = 102
        )
    )

    val initPersonalSettingList: List<LocalPersonalSettingEntity> = initLocalTotalSettingList.map {
        LocalPersonalSettingEntity(
            menuId = it.menuId,
            isEnabled = it.isInitEnabled,
            customData = null
        )
    }
}