package com.basetrack.basetrackqr.login.network.data.requestData


data class LoginModel(var email : String, var password: String) {
    override fun toString(): String {
        return "{\n\"email\": \"$email\",\n\"password\": \"$password\"\n}\n"

    }

}