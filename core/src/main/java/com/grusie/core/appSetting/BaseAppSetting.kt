package com.grusie.core.appSetting

import kotlinx.serialization.Serializable

@Serializable
abstract class BaseAppSetting {
    abstract val parsingData: Any
}