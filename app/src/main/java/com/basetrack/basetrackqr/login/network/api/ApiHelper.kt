package com.basetrack.basetrackqr.login.network.api

import com.basetrack.basetrackqr.login.network.data.requestData.LoginModel
import com.basetrack.basetrackqr.login.network.data.responseData.UserLoginResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun userLogin( body: LoginModel?): Response<UserLoginResponse>
}