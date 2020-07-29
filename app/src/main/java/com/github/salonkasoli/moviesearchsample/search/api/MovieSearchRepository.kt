package com.github.salonkasoli.moviesearchsample.search.api

import android.content.Context
import com.github.salonkasoli.moviesearchsample.Const
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieSearchRepository(
    context: Context
) {

    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Const.MOVIE_DB_URL)
        .build()

    suspend fun searchMovie(
        query: String,
        page: Int
    ): RepoResponse<MovieSearchResponse> = withContext(Dispatchers.IO) {
        val res: ExecutionResult<MovieSearchResponse> = retrofit.create(MovieSearchApi::class.java)
            .searchMovie(query, page, apiKey)
            .executeSafe()

        if (res is ExecutionError) {
            return@withContext RepoError<MovieSearchResponse>(res.exception)
        }

        val response: Response<MovieSearchResponse> = (res as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<MovieSearchResponse>(IllegalStateException("response = $response, body = ${response.body()}"))
        }

        return@withContext RepoSuccess(response.body()!!)
    }
}