package com.basetrack.basetrackqr.login.network.data.responseData

import com.google.gson.annotations.SerializedName



data class User (

	@SerializedName("id") val id : Long,
	@SerializedName("org") val org : Org,
	@SerializedName("name") val name : String,
	@SerializedName("last_name") val last_name : String,
	@SerializedName("email") val email : String,
	@SerializedName("status") val status : Int,
	@SerializedName("created") val created : String,
	@SerializedName("modified") val modified : String,
	@SerializedName("last_visit") val last_visit : String,
	@SerializedName("is_deleted") val is_deleted : Boolean,
	@SerializedName("roles") val roles : Int,
	@SerializedName("notes") val notes : List<String>
)