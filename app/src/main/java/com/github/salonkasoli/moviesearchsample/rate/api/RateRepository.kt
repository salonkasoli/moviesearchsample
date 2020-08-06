package com.github.salonkasoli.moviesearchsample.rate.api

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.core.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class RateRepository @Inject constructor(
    private val retrofit: Retrofit,
    context: Context,
    private val sessionIdCache: SessionIdCache
) {
    private val apiKey = context.getString(R.string.moviedb_api_key)

    suspend fun getSession(
        movieId: Int, rate: Float
    ): RepoResponse<RateResponse> = withContext(Dispatchers.IO) {
        val result: ExecutionResult<RateResponse> = retrofit.create(RateApi::class.java)
            .rate(movieId, apiKey, sessionIdCache.getSessionId(), RateRequest(rate))
            .executeSafe()

        if (result is ExecutionError) {
            return@withContext RepoError<RateResponse>(
                result.exception
            )
        }

        val response: Response<RateResponse> = (result as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<RateResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        val rateResponse: RateResponse = response.body()!!

        if (rateResponse.statusCode != 1) {
            return@withContext RepoError<RateResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        return@withContext RepoSuccess(rateResponse)
    }
}