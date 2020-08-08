package com.github.salonkasoli.moviesearchsample.auth

import androidx.lifecycle.*
import com.github.salonkasoli.moviesearchsample.auth.session.Session
import com.github.salonkasoli.moviesearchsample.auth.session.SessionRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenResponse
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.core.mvvm.SimpleEvent
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel(
    private val newTokenRepository: NewTokenRepository,
    private val authedTokenRepository: AuthedTokenRepository,
    private val sessionRepository: SessionRepository,
    private val sessionIdCache: SessionIdCache,
    private val movieDetailCache: MovieDetailCache
) : ViewModel() {

    val errorEvent: LiveData<SimpleEvent>
        get() = _errorEvent
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _errorEvent = MutableLiveData<SimpleEvent>()
    private val _loadingState = MutableLiveData<LoadingState>()

    fun createSessionId(login: String, password: String) = viewModelScope.launch {
        _loadingState.postValue(LoadingState.LOADING)
        val tokenResponse: RepoResponse<NewTokenResponse> = newTokenRepository.getNewToken()
        if (tokenResponse is RepoError) {
            _errorEvent.postValue(SimpleEvent())
            _loadingState.postValue(LoadingState.WAITING)
            return@launch
        }
        tokenResponse as RepoSuccess

        val authedTokenResponse: RepoResponse<AuthedTokenResponse> =
            authedTokenRepository.createAuthedToken(login, password, tokenResponse.data.token)

        if (authedTokenResponse is RepoError) {
            _errorEvent.postValue(SimpleEvent())
            _loadingState.postValue(LoadingState.WAITING)
            return@launch
        }
        authedTokenResponse as RepoSuccess

        val sessionResponse: RepoResponse<Session> = sessionRepository.getSession(
            authedTokenResponse.data.requestToken
        )

        when (sessionResponse) {
            is RepoError -> {
                _errorEvent.postValue(SimpleEvent())
                _loadingState.postValue(LoadingState.WAITING)
            }
            is RepoSuccess -> {
                movieDetailCache.clear()
                sessionIdCache.setSessionId(sessionResponse.data.sessionId)
                _loadingState.postValue(LoadingState.SUCCESS)
            }
        }
    }

    class Factory @Inject constructor(
        private val newTokenRepository: NewTokenRepository,
        private val authedTokenRepository: AuthedTokenRepository,
        private val sessionRepository: SessionRepository,
        private val sessionIdCache: SessionIdCache,
        private val movieDetailCache: MovieDetailCache
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(
                newTokenRepository,
                authedTokenRepository,
                sessionRepository,
                sessionIdCache,
                movieDetailCache
            ) as T
        }
    }
}