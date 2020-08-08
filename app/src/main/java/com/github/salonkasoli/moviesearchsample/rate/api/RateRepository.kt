package com.github.salonkasoli.moviesearchsample.rate.api

import android.content.Context
import androidx.annotation.WorkerThread
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class RateRepository @Inject constructor(
    private val retrofit: Retrofit,
    context: Context,
    private val sessionIdCache: SessionIdCache
) {
    private val apiKey = context.getString(R.string.moviedb_api_key)

    @Throws(Exception::class)
    @WorkerThread
    fun postRate(movieId: Int, rate: Float): RateResponse {
        val response: Response<RateResponse>? = retrofit.create(RateApi::class.java)
            .rate(movieId, apiKey, sessionIdCache.getSessionId(), RateRequest(rate))
            .execute()

        if (response?.isSuccessful == false || response?.body() == null) {
            throw IllegalStateException("response = $response, body = ${response?.body()}")
        }

        val rateResponse: RateResponse = response.body()!!

        if (rateResponse.statusCode != 1) {
            throw IllegalStateException("response = $response, body = ${response.body()}")
        }

        return rateResponse
    }
}