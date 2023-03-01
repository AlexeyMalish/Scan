package com.basetrack.basetrackqr.login.network.repo

import com.basetrack.basetrackqr.login.network.api.ApiHelper
import com.basetrack.basetrackqr.login.network.data.requestData.LoginModel
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiHelper: ApiHelper){
    suspend fun loginUser(loginModel : LoginModel) = apiHelper.userLogin(loginModel)
}