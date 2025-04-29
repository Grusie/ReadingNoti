package com.grusie.domain.data

sealed class PersonalSettingException : Exception() {
    data object NotFoundOnServer : PersonalSettingException()
}