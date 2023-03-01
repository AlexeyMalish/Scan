package com.basetrack.basetrackqr.db.gsonModel

import com.google.gson.annotations.SerializedName

data class SaveModel(
    @SerializedName("первозчик") val transporter : String,
    @SerializedName("компания") val company : String,
    @SerializedName("дата") val date : String,
    @SerializedName("комлекты") val comolect : ArrayList<Code>?
)

data class Code(
    @SerializedName("баркод") val barcode: String,
    @SerializedName("qrКод") val qrCode : String
)