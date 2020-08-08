package com.github.salonkasoli.moviesearchsample.auth

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
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
    private val movieDetailCache: MovieDetailCache,
    savedStateRegistry: SavedStateRegistry
) : ViewModel() {

    val errorEvent: LiveData<SimpleEvent>
        get() = _errorEvent
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _errorEvent = MutableLiveData<SimpleEvent>()
    private val _loadingState = MutableLiveData<LoadingState>()

    init {
        savedStateRegistry.consumeRestoredStateForKey(BUNDLE_KEY)?.let {
            _errorEvent.value = SimpleEvent(it.getBoolean(BUNDLE_ERROR_EVENT, true))
            _loadingState.value = LoadingState.values().get(it.getInt(BUNDLE_STATE_ORDINAL))
        }
        savedStateRegistry.registerSavedStateProvider(BUNDLE_KEY, {
            return@registerSavedStateProvider Bundle().apply {
                _errorEvent.value?.hasBeenHandled?.let { putBoolean(BUNDLE_ERROR_EVENT, it) }
                _loadingState.value?.ordinal?.let { putInt(BUNDLE_STATE_ORDINAL, it) }
            }
        })
    }

    fun createSessionId(login: String, password: String) = viewModelScope.launch {
        _loadingState.postValue(LoadingState.LOADING)
        val tokenResponse: RepoResponse<NewTokenResponse> = newTokenRepository.getNewToken()
        if (tokenResponse is RepoError) {
            _errorEvent.postValue(SimpleEvent())
            _loadingState.postValue(LoadingState.WAITINIG)
            return@launch
        }
        tokenResponse as RepoSuccess

        val authedTokenResponse: RepoResponse<AuthedTokenResponse> =
            authedTokenRepository.createAuthedToken(login, password, tokenResponse.data.token)

        if (authedTokenResponse is RepoError) {
            _errorEvent.postValue(SimpleEvent())
            _loadingState.postValue(LoadingState.WAITINIG)
            return@launch
        }
        authedTokenResponse as RepoSuccess

        val sessionResponse: RepoResponse<Session> = sessionRepository.getSession(
            authedTokenResponse.data.requestToken
        )

        when (sessionResponse) {
            is RepoError -> {
                _errorEvent.postValue(SimpleEvent())
                _loadingState.postValue(LoadingState.WAITINIG)
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
        private val movieDetailCache: MovieDetailCache,
        private val savedStateRegistry: SavedStateRegistry
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(
                newTokenRepository,
                authedTokenRepository,
                sessionRepository,
                sessionIdCache,
                movieDetailCache,
                savedStateRegistry
            ) as T
        }
    }

    companion object {
        private const val BUNDLE_KEY = "auth_view_model"
        private const val BUNDLE_ERROR_EVENT = "error_event"
        private const val BUNDLE_STATE_ORDINAL = "state_ordinal"
    }
}