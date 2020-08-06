package com.github.salonkasoli.moviesearchsample.auth.session

import androidx.lifecycle.LifecycleCoroutineScope
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenResponse
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Интерактор, с помощью которого можно залогиниться в приложении.
 *
 * Схема логина следующая:
 * 1) Генерируем request токен
 * 2) На его основе с помощью логина + пароля генерируем авторизованный request token
 * 3) Генерируем session id и кэшируем его
 *
 * Далее session id это токен, с которым будем ходить к остальным АПИ.
 */
class SessionInteractor @Inject constructor(
    private val newTokenRepository: NewTokenRepository,
    private val authedTokenRepository: AuthedTokenRepository,
    private val sessionRepository: SessionRepository,
    private val sessionIdCache: SessionIdCache,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
) {

    /**
     * Дернется, если обосрамс произошел на любом этапе формирования sessionId.
     */
    var errorListener: (() -> Unit)? = null

    /**
     * Дернется, когда юзер успешно авторизовался.
     * Session Id можно будет получить по всему приложению у [SessionIdCache].
     */
    var successListener: (() -> Unit)? = null

    fun createSessionId(
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
            authedTokenRepository.createAuthedToken(login, password, tokenResponse.data.token)

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
                is RepoError -> {
                    errorListener?.invoke()
                }
                is RepoSuccess -> {
                    sessionIdCache.setSessionId(sessionResponse.data.sessionId)
                    successListener?.invoke()
                }
            }
        }
    }
}