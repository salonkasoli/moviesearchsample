package com.github.salonkasoli.moviesearchsample.auth.token.authed

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthedTokenApi {

    @POST("authentication/token/validate_with_login")
    fun createSessionWithLogin(
        @Body authedTokenRequest: AuthedTokenRequest,
        @Query("api_key") apiKey: String
    ): Call<AuthedTokenResponse>
}