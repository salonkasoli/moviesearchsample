package com.github.salonkasoli.moviesearchsample.auth.token.authed

import com.google.gson.annotations.SerializedName

data class AuthedTokenResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("request_token")
    val requestToken: String
)