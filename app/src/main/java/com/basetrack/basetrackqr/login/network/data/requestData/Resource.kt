package com.basetrack.basetrackqr.login.network.data.requestData

import okhttp3.ResponseBody

sealed class Resource<out T> (
    val data: T? = null,
    val message: String? = null
        ){
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    object Loading : Resource<Nothing>()
}