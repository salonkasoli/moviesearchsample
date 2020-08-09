package com.github.salonkasoli.moviesearchsample.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.auth.session.SessionRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenResponse
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenResponse
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.core.mvvm.SimpleEvent
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AuthViewModel(
    private val newTokenRepository: NewTokenRepository,
    private val authedTokenRepository: AuthedTokenRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val errorEvent: LiveData<SimpleEvent>
        get() = _errorEvent
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _errorEvent = MutableLiveData<SimpleEvent>()
    private val _loadingState = MutableLiveData<LoadingState>()

    private val compositeDisposable = CompositeDisposable()

    fun createSessionId(login: String, password: String) {
        _loadingState.postValue(LoadingState.LOADING)
        val disposable = Single.fromCallable({
            return@fromCallable newTokenRepository.getNewToken()
        })
            .subscribeOn(Schedulers.io())
            .map { tokenResponse: NewTokenResponse ->
                authedTokenRepository.createAuthedToken(login, password, tokenResponse.token)
            }
            .map { authedTokenResponse: AuthedTokenResponse ->
                sessionRepository.getSession(authedTokenResponse.requestToken)
            }
            .subscribe(
                {
                    _loadingState.postValue(LoadingState.SUCCESS)
                },
                {
                    _errorEvent.postValue(SimpleEvent())
                    _loadingState.postValue(LoadingState.WAITING)
                }
            )
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    class Factory @Inject constructor(
        private val newTokenRepository: NewTokenRepository,
        private val authedTokenRepository: AuthedTokenRepository,
        private val sessionRepository: SessionRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(
                newTokenRepository,
                authedTokenRepository,
                sessionRepository
            ) as T
        }
    }
}