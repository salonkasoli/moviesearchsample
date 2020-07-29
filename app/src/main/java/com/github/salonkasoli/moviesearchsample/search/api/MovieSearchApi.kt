package com.github.salonkasoli.moviesearchsample.search.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieSearchApi {

    @GET("search/movie")
    fun searchMovie(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU"
    ): Call<MovieSearchResponse>
}