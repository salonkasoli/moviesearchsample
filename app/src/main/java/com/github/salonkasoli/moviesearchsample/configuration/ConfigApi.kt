package com.github.salonkasoli.moviesearchsample.configuration

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ConfigApi {

    @GET("configuration")
    fun getConfiguration(
        @Query("api_key") apiKey: String
    ) : Call<Config>
}