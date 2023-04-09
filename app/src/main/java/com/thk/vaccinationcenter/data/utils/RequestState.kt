package com.thk.vaccinationcenter.data.utils

import retrofit2.HttpException

sealed class RequestState {
    object Success : RequestState()
    object NetworkError : RequestState()
    object ServerError : RequestState()

    companion object {
        /**
         * [HttpException]의 code에 따라 알맞은 Error를 리턴
         */
        fun errorOfCode(code: Int) = when (code) {
            in 400..499 -> NetworkError
            in 500..599 -> ServerError
            else -> NetworkError
        }
    }
}
