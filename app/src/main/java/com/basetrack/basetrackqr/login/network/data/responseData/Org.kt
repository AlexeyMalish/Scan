package com.basetrack.basetrackqr.login.network.data.responseData

import com.google.gson.annotations.SerializedName

data class Org (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String
)