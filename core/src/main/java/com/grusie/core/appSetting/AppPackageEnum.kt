package com.grusie.core.appSetting

import com.grusie.core.utils.LoggerProvider
import kotlinx.serialization.json.Json

/**
 * 패키지를 기준으로 앱을 구분하여 처리 할 때 사용 할 EnumClass
 * 구분이 필요할 때 추가하여 사용하면 되기에 서버와 1:1일 필요는 없음
 */
enum class AppPackageEnum(val packageName: String) {
    NONE(""),
    KAKAO("com.kakao.talk"),
    DOUZONE("com.douzone.bizbox.klago.app");

    companion object {
        fun from(packageName: String): AppPackageEnum {
            return entries.firstOrNull { it.packageName == packageName } ?: NONE
        }

        fun getCustomData(packageName: String, jsonCustomData: String): BaseAppSetting? {
            return when (from(packageName)) {
                KAKAO -> {
                    try {
                        KakaoAppSetting(Json.decodeFromString<KakaoAppSettingData>(jsonCustomData))
                    } catch (e: Exception) {
                        LoggerProvider.logger.e(
                            "AppPackageEnum getCustomData Error",
                            "${e.message}"
                        )
                        null
                    }
                }

                else -> null
            }
        }

        fun getAppSettingJson(packageName: String): String? {
            val json = Json {
                encodeDefaults = true
            }
            return when (from(packageName)) {
                KAKAO -> {
                    try {
                        json.encodeToString(KakaoAppSettingData())
                    } catch (e: Exception) {
                        LoggerProvider.logger.e(
                            "AppPackageEnum getAppSettingJson Error",
                            "${e.message}"
                        )
                        null
                    }
                }

                else -> null
            }
        }
    }
}