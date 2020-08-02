package com.github.salonkasoli.moviesearchsample.rate.api

import com.google.gson.annotations.SerializedName

data class RateResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("status_message")
    val statusMessage: String
)