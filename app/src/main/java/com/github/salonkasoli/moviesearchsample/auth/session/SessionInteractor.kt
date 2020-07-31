package com.github.salonkasoli.moviesearchsample.auth.session

import androidx.lifecycle.LifecycleCoroutineScope
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenResponse
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionInteractor(
    private val newTokenRepository: NewTokenRepository,
    private val authedTokenRepository: AuthedTokenRepository,
    private val sessionRepository: SessionRepository,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
) {

    var errorListener: (() -> Unit)? = null
    var successListener: ((sessionId: String) -> Unit)? = null

    fun getSessionId(
        login: String,
        password: String
    ) = lifecycleCoroutineScope.launchWhenCreated {
        val tokenResponse: RepoResponse<NewTokenResponse> = newTokenRepository.getNewToken()
        if (tokenResponse is RepoError) {
            withContext(Dispatchers.Main) {
                errorListener?.invoke()
            }
            return@launchWhenCreated
        }
        tokenResponse as RepoSuccess

        val authedTokenResponse: RepoResponse<AuthedTokenResponse> =
            authedTokenRepository.createNewSession(login, password, tokenResponse.data.token)

        if (authedTokenResponse is RepoError) {
            withContext(Dispatchers.Main) {
                errorListener?.invoke()
            }
            return@launchWhenCreated
        }
        authedTokenResponse as RepoSuccess

        val sessionResponse: RepoResponse<Session> = sessionRepository.getSession(
            authedTokenResponse.data.requestToken
        )

        withContext(Dispatchers.Main) {
            when (sessionResponse) {
                is RepoError -> errorListener?.invoke()
                is RepoSuccess -> successListener?.invoke(sessionResponse.data.sessionId)
            }
        }
    }

}