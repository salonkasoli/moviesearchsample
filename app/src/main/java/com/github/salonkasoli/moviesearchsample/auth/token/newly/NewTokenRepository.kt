package com.github.salonkasoli.moviesearchsample.auth.token.newly

import android.content.Context
import androidx.annotation.WorkerThread
import com.github.salonkasoli.moviesearchsample.R
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewTokenRepository @Inject constructor(
    private val retrofit: Retrofit,
    context: Context
) {

    private val apiKey = context.getString(R.string.moviedb_api_key)

    @Throws(Exception::class)
    @WorkerThread
    fun getNewToken(): NewTokenResponse {
        val response: Response<NewTokenResponse> = retrofit.create(NewTokenApi::class.java)
            .createNewAuthToken(apiKey)
            .execute()

        if (!response.isSuccessful || response.body() == null) {
            throw IllegalStateException("response = $response, body = ${response.body()}")
        }

        val newTokenResponse: NewTokenResponse = response.body()!!

        if (!newTokenResponse.success) {
            throw IllegalStateException("response = $response, body = ${response.body()}")
        }

        return newTokenResponse
    }
}