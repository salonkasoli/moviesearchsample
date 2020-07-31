package com.github.salonkasoli.moviesearchsample.auth.session

import android.content.Context
import com.github.salonkasoli.moviesearchsample.Const
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SessionRepository(
    context: Context
) {
    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Const.MOVIE_DB_URL)
        .build()

    suspend fun getSession(
        requestToken: String
    ): RepoResponse<Session> = withContext(Dispatchers.IO) {
        val result: ExecutionResult<Session> = retrofit.create(SessionApi::class.java)
            .createSession(apiKey, SessionRequest(requestToken))
            .executeSafe()

        if (result is ExecutionError) {
            return@withContext RepoError<Session>(
                result.exception
            )
        }

        val response: Response<Session> = (result as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<Session>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        val session: Session = response.body()!!

        if (!session.success) {
            return@withContext RepoError<Session>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        return@withContext RepoSuccess(session)
    }
}