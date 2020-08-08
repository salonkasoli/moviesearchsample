package com.github.salonkasoli.moviesearchsample.rate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.core.mvvm.SimpleEvent
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import com.github.salonkasoli.moviesearchsample.rate.api.RateRepository
import com.github.salonkasoli.moviesearchsample.rate.api.RateResponse
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    private val compositeDisposable = CompositeDisposable()

    fun rateMovie(movieId: Int, rate: Float) {
        val disposable = Single.create<RateResponse>({ emitter ->
            _state.postValue(LoadingState.LOADING)
            emitter.onSuccess(rateRepository.postRate(movieId, rate))
        })
            .subscribeOn(Schedulers.io())
            .subscribe(
                { response: RateResponse? ->
                    movieDetailCache.clear()
                    _state.postValue(LoadingState.SUCCESS)
                },
                {
                    _errorEvent.postValue(SimpleEvent())
                    _state.postValue(LoadingState.WAITING)
                }
            )
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
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