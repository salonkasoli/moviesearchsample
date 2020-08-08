package com.github.salonkasoli.moviesearchsample.rate

import androidx.lifecycle.*
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
    private val movieDetailCache: MovieDetailCache
) : ViewModel() {

    val errorEvent: LiveData<SimpleEvent>
        get() = _errorEvent
    val state: LiveData<LoadingState>
        get() = _state

    private val _state = MutableLiveData<LoadingState>()
    private val _errorEvent = MutableLiveData<SimpleEvent>()

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
                _state.postValue(LoadingState.WAITING)
            }
        }
    }

    class Factory @Inject constructor(
        private val rateRepository: RateRepository,
        private val movieDetailCache: MovieDetailCache
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RateViewModel(rateRepository, movieDetailCache) as T
        }
    }
}