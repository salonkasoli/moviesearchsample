package com.github.salonkasoli.moviesearchsample.detail.api

import android.content.Context
import android.util.Log
import com.github.salonkasoli.moviesearchsample.Const
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieDetailRepository(
    context: Context
) {

    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Const.MOVIE_DB_URL)
        .build()

    suspend fun getMovieDetails(
        id: Int
    ): RepoResponse<MovieDetailNetworkModel> = withContext(Dispatchers.IO) {
        val res: ExecutionResult<MovieDetailNetworkModel> =
            retrofit.create(MovieDetailApi::class.java)
                .getMovieDetail(id, apiKey)
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

        Log.wtf("lol", "got movie detail ${response.body()!!}")

        return@withContext RepoSuccess(
            response.body()!!
        )
    }

}