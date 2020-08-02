package com.github.salonkasoli.moviesearchsample.rate.api

import com.google.gson.annotations.SerializedName

data class RateRequest(
    // 0.5 - 10.0
    @SerializedName("value")
    val value: Float
)