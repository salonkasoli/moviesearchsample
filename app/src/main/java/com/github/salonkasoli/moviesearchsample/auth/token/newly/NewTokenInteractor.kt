package com.github.salonkasoli.moviesearchsample.auth.token.newly

import androidx.lifecycle.LifecycleCoroutineScope
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewTokenInteractor(
    private val tokenRepository: NewTokenRepository,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
) {

    var errorListener: (() -> Unit)? = null
    var successListener: ((token: String) -> Unit)? = null

    fun getNewToken() = lifecycleCoroutineScope.launchWhenCreated {
        val response: RepoResponse<NewTokenResponse> = tokenRepository.getNewToken()

        withContext(Dispatchers.Main) {
            when (response) {
                is RepoError -> errorListener?.invoke()
                is RepoSuccess -> successListener?.invoke(response.data.token)
            }
        }
    }
}