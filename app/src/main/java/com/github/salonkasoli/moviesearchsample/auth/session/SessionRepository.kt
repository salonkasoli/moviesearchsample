package com.github.salonkasoli.moviesearchsample.auth.session

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionIdCache: SessionIdCache,
    private val movieDetailCache: MovieDetailCache,
    private val retrofit: Retrofit,
    context: Context
) {
    private val apiKey = context.getString(R.string.moviedb_api_key)

    fun getSession(requestToken: String): Session {
        val response: Response<Session> = retrofit.create(SessionApi::class.java)
            .createSession(apiKey, SessionRequest(requestToken))
            .execute()

        if (!response.isSuccessful || response.body() == null) {
            throw IllegalStateException("response = $response, body = ${response.body()}")
        }

        val session: Session = response.body()!!

        if (!session.success) {
            throw IllegalStateException("response = $response, body = ${response.body()}")
        }

        sessionIdCache.setSessionId(session.sessionId)
        // После авторизации надо заного подгрузить фильмы. Там может появиться инфа об оценке.
        movieDetailCache.clear()
        return session
    }
}