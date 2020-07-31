package com.github.salonkasoli.moviesearchsample.auth.token.authed

import com.google.gson.annotations.SerializedName

data class AuthedTokenRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("request_token")
    val requestToken: String
)