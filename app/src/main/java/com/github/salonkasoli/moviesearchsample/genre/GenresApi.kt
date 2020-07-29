package com.github.salonkasoli.moviesearchsample.genre

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GenresApi {

    @GET("genre/movie/list")
    fun getMovieGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU"
    ): Call<GenreResponse>
}