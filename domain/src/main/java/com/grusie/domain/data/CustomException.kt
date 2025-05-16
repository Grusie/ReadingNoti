package com.grusie.domain.data

sealed class CustomException : Exception() {
    data object NotFoundOnServer : CustomException()
    data object NetworkError : CustomException()
    data object DataMatchingError : CustomException()
}