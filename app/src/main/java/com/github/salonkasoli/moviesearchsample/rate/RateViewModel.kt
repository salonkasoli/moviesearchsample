package com.github.salonkasoli.moviesearchsample.rate

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.core.mvvm.SimpleEvent
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import com.github.salonkasoli.moviesearchsample.rate.api.RateRepository
import com.github.salonkasoli.moviesearchsample.rate.api.RateResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

class RateViewModel(
    private val rateRepository: RateRepository,
    private val movieDetailCache: MovieDetailCache,
    private val savedStateRegistry: SavedStateRegistry
) : ViewModel() {

    val errorEvent: LiveData<SimpleEvent>
        get() = _errorEvent
    val state: LiveData<LoadingState>
        get() = _state

    private val _state = MutableLiveData<LoadingState>()
    private val _errorEvent = MutableLiveData<SimpleEvent>()

    init {
        savedStateRegistry.consumeRestoredStateForKey(BUNDLE_KEY)?.let {
            _errorEvent.value = SimpleEvent(it.getBoolean(BUNDLE_ERROR_EVENT, true))
            _state.value = LoadingState.values().get(it.getInt(BUNDLE_STATE_ORDINAL))
        }
        savedStateRegistry.registerSavedStateProvider(BUNDLE_KEY, {
            return@registerSavedStateProvider Bundle().apply {
                _errorEvent.value?.hasBeenHandled?.let { putBoolean(BUNDLE_ERROR_EVENT, it) }
                _state.value?.ordinal?.let { putInt(BUNDLE_STATE_ORDINAL, it) }
            }
        })
    }

    fun rateMovie(movieId: Int, rate: Float) = viewModelScope.launch {
        _state.postValue(LoadingState.LOADING)
        val tokenResponse: RepoResponse<RateResponse> = rateRepository.getSession(movieId, rate)
        when (tokenResponse) {
            is RepoSuccess -> {
                movieDetailCache.clear()
                _state.postValue(LoadingState.SUCCESS)
            }
            else -> {
                _errorEvent.postValue(SimpleEvent())
                _state.postValue(LoadingState.WAITINIG)
            }
        }
    }

    class Factory @Inject constructor(
        private val rateRepository: RateRepository,
        private val movieDetailCache: MovieDetailCache,
        private val savedStateRegistry: SavedStateRegistry
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RateViewModel(rateRepository, movieDetailCache, savedStateRegistry) as T
        }
    }

    companion object {
        private const val BUNDLE_KEY = "rate_view_model"
        private const val BUNDLE_ERROR_EVENT = "error_event"
        private const val BUNDLE_STATE_ORDINAL = "state_ordinal"
    }
}