package com.github.salonkasoli.moviesearchsample.auth.token.authed

import android.content.Context
import com.github.salonkasoli.moviesearchsample.Const
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthedTokenRepository(
    context: Context
) {
    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Const.MOVIE_DB_URL)
        .build()

    suspend fun createNewSession(
        login: String,
        password: String,
        requestToken: String
    ): RepoResponse<AuthedTokenResponse> = withContext(Dispatchers.IO) {
        val result: ExecutionResult<AuthedTokenResponse> =
            retrofit.create(AuthedTokenApi::class.java)
                .createSessionWithLogin(
                    AuthedTokenRequest(
                        login,
                        password,
                        requestToken
                    ),
                    apiKey
                )
                .executeSafe()

        if (result is ExecutionError) {
            return@withContext RepoError<AuthedTokenResponse>(
                result.exception
            )
        }

        val response: Response<AuthedTokenResponse> = (result as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<AuthedTokenResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        val authedTokenResponse: AuthedTokenResponse = response.body()!!

        if (!authedTokenResponse.success) {
            return@withContext RepoError<AuthedTokenResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        return@withContext RepoSuccess(authedTokenResponse)
    }
}