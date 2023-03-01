package com.basetrack.basetrackqr.login.network.data.responseData

import com.google.gson.annotations.SerializedName

data class UserLoginResponse (

	@SerializedName("token") val token : String,
	@SerializedName("user") val user : User
)

