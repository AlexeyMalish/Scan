package com.basetrack.basetrackqr.login.network.api

import com.basetrack.basetrackqr.login.network.data.requestData.LoginModel
import com.basetrack.basetrackqr.login.network.data.responseData.UserLoginResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val interfaceApi: InterfaceApi) : ApiHelper {
    override suspend fun userLogin(body: LoginModel?): Response<UserLoginResponse> = interfaceApi.userLogin(body)
}