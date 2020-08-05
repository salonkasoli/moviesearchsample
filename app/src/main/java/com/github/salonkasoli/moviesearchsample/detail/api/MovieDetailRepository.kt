package com.github.salonkasoli.moviesearchsample.detail.api

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.core.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailRepository @Inject constructor(
    private val retrofit: Retrofit,
    context: Context,
    private val sessionIdCache: SessionIdCache
) {

    private val apiKey = context.getString(R.string.moviedb_api_key)

    suspend fun getMovieDetails(
        id: Int
    ): RepoResponse<MovieDetailNetworkModel> = withContext(Dispatchers.IO) {
        val res: ExecutionResult<MovieDetailNetworkModel> =
            retrofit.create(MovieDetailApi::class.java)
                .getMovieDetail(id, apiKey, sessionIdCache.getSessionId())
                .executeSafe()

        if (res is ExecutionError) {
            return@withContext RepoError<MovieDetailNetworkModel>(
                res.exception
            )
        }

        val response: Response<MovieDetailNetworkModel> = (res as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<MovieDetailNetworkModel>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        return@withContext RepoSuccess(
            response.body()!!
        )
    }

}