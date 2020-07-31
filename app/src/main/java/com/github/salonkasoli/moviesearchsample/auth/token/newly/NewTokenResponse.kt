package com.github.salonkasoli.moviesearchsample.auth.token.newly

import com.google.gson.annotations.SerializedName

data class NewTokenResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("request_token")
    val token: String
)