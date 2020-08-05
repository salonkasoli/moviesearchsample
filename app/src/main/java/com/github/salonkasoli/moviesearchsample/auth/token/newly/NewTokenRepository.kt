package com.github.salonkasoli.moviesearchsample.auth.token.newly

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun getNewToken(): RepoResponse<NewTokenResponse> = withContext(Dispatchers.IO) {
        val result: ExecutionResult<NewTokenResponse> = retrofit.create(NewTokenApi::class.java)
            .createNewAuthToken(apiKey)
            .executeSafe()

        if (result is ExecutionError) {
            return@withContext RepoError<NewTokenResponse>(
                result.exception
            )
        }

        val response: Response<NewTokenResponse> = (result as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<NewTokenResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        val newTokenResponse: NewTokenResponse = response.body()!!

        if (!newTokenResponse.success) {
            return@withContext RepoError<NewTokenResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        return@withContext RepoSuccess(newTokenResponse)
    }
}