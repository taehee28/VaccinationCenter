package com.thk.vaccinationcenter.data.utils

sealed class RequestState {
    object Success : RequestState()
    object NetworkError : RequestState()
    object ServerError : RequestState()
}
