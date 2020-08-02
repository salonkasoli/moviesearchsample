package com.github.salonkasoli.moviesearchsample.rate.api

import android.content.Context
import android.util.Log
import com.github.salonkasoli.moviesearchsample.Const
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.core.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RateRepository(
    context: Context,
    private val sessionIdCache: SessionIdCache
) {
    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Const.MOVIE_DB_URL)
        .build()

    suspend fun getSession(
        movieId: Int, rate: Float
    ): RepoResponse<RateResponse> = withContext(Dispatchers.IO) {
        val result: ExecutionResult<RateResponse> = retrofit.create(RateApi::class.java)
            .rate(movieId, apiKey, sessionIdCache.getSessionId(), RateRequest(rate))
            .executeSafe()

        Log.wtf("lol", "get rate result $result")

        if (result is ExecutionError) {
            return@withContext RepoError<RateResponse>(
                result.exception
            )
        }

        val response: Response<RateResponse> = (result as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            Log.wtf("lol", "response = $response, body = ${response.body()}")
            return@withContext RepoError<RateResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        val rateResponse: RateResponse = response.body()!!

        Log.wtf("lol", "get rate response $rateResponse")

        if (rateResponse.statusCode != 1) {
            return@withContext RepoError<RateResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        return@withContext RepoSuccess(rateResponse)
    }
}