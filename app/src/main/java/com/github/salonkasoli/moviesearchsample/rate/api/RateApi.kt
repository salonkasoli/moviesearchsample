package com.github.salonkasoli.moviesearchsample.rate.api

import retrofit2.Call
import retrofit2.http.*

interface RateApi {

    @POST("movie/{movie_id}/rating")
    fun rate(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String?,
        @Query("session_id") sessionId: String?,
        @Body rateRequest: RateRequest,
        @Header("Content-type") contentType: String = "application/json;charset=utf-8"
    ): Call<RateResponse>
}