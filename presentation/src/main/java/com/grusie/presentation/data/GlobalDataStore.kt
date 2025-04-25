package com.grusie.presentation.data

/**
 * 앱 전역에서 사용하는 데이터 들을 담당하는 클래스
 * 모든 데이터는 getter, setter를 따로 구현하여 안정성을 높히는 형태로 구현
 */
object GlobalDataStore {
    private var notiTypeList: List<NotiType> = emptyList()
    private var notiSettingMap: Map<Int, NotiType> = mapOf()


    fun getNotiTypeList(): List<NotiType> = notiTypeList
    fun setNotiTypeList(notiTypeList: List<NotiType>) {
        this.notiTypeList = notiTypeList
    }

    fun getNotiSetting(notiTypeId: Int) {
        notiSettingMap.getOrDefault(notiTypeId, NotiType())
    }

}