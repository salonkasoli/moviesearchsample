package com.github.salonkasoli.moviesearchsample.detail.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDetailApi {

    @GET("movie/{movie_id}")
    fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Query("language") language: String = "ru-RU",
        @Query("append_to_response") appendToResponse: String = "account_states"
    ): Call<MovieDetailNetworkModel>
}