package com.grusie.domain.data

sealed class CommonException : Exception() {
    data object NotFoundOnServer : CommonException()
    data object NetworkError : CommonException()
    data object DataMatchingError : CommonException()
    data object EssentialError : CommonException()
}

sealed class AuthException : Exception() {
    data object EmailTypeMatchingError : AuthException()
    data object PwConfirmIncorrectError : AuthException()
    data object EmailPwIncorrectError : AuthException()
    data object PwLengthError : AuthException()
    data object DuplicationEmailError : AuthException()

    companion object {
       const val EXTRA_PW_MIN_LENGTH = 6
    }
}