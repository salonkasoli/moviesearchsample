package com.github.salonkasoli.moviesearchsample.auth.token.newly

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewTokenApi {

    @GET("authentication/token/new")
    fun createNewAuthToken(
        @Query("api_key") apiKey: String
    ): Call<NewTokenResponse>
}