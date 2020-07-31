package com.github.salonkasoli.moviesearchsample.auth.session

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SessionApi {

    @POST("authentication/session/new")
    fun createSession(
        @Query("api_key") apiKey: String,
        @Body sessionRequest: SessionRequest
    ): Call<Session>
}