package com.basetrack.basetrackqr.login.network.api

import com.basetrack.basetrackqr.login.network.data.requestData.LoginModel
import com.basetrack.basetrackqr.login.network.data.responseData.UserLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InterfaceApi {
    @POST("user/login")
    suspend fun userLogin(@Body body: LoginModel?): Response<UserLoginResponse>
}