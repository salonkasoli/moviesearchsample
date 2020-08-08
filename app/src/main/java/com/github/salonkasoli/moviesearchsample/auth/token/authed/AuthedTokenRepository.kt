package com.github.salonkasoli.moviesearchsample.auth.token.authed

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthedTokenRepository @Inject constructor(
    private val retrofit: Retrofit,
    context: Context
) {
    private val apiKey = context.getString(R.string.moviedb_api_key)

    fun createAuthedToken(
        login: String,
        password: String,
        requestToken: String
    ): AuthedTokenResponse {
        val response: Response<AuthedTokenResponse> = retrofit.create(AuthedTokenApi::class.java)
            .createSessionWithLogin(
                AuthedTokenRequest(login, password, requestToken),
                apiKey
            )
            .execute()

        if (!response.isSuccessful || response.body() == null) {
            throw IllegalStateException("response = $response, body = ${response.body()}")
        }

        val authedTokenResponse: AuthedTokenResponse = response.body()!!

        if (!authedTokenResponse.success) {
            throw IllegalStateException("response = $response, body = ${response.body()}")
        }

        return authedTokenResponse
    }
}