package com.grusie.presentation.data

// 대체어를 관리할 데이터 클래스
data class AlternativeData(
    val id: Int = -1,    // 각 데이터들을 나눌 아이디값 -> 리스트의 순서대로 들어갈 듯
    val isRepeatAlter: Boolean = false,   // 반복되는 것이 존재 할 떄 끝 날 때까지를 묶어서 하나로 처리 할 것인지 여부 ex) 'ㅋㅋㅋ' -> '키킥' => 'ㅋㅋㅋㅋㅋㅋ' -> '키킥'
    val originContent: String = "",  // 대체 처리 할 문장 or 단어
    val alternativeContent: String = "" // 대체어
)