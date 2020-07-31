package com.github.salonkasoli.moviesearchsample.auth.session

import com.google.gson.annotations.SerializedName

data class SessionRequest(
    @SerializedName("request_token")
    val requestToken: String
)