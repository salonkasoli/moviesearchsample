package com.github.salonkasoli.moviesearchsample.rate.api

import androidx.lifecycle.LifecycleCoroutineScope
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RateInteractor(
    private val rateRepository: RateRepository,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
) {

    var errorListener: (() -> Unit)? = null
    var successListener: (() -> Unit)? = null

    fun rateMovie(
        movieId: Int, rate: Float
    ) = lifecycleCoroutineScope.launchWhenCreated {
        val tokenResponse: RepoResponse<RateResponse> = rateRepository.getSession(movieId, rate)
        withContext(Dispatchers.Main) {
            when (tokenResponse) {
                is RepoSuccess -> successListener?.invoke()
                else -> errorListener?.invoke()
            }
        }
    }
}