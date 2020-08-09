package com.github.salonkasoli.moviesearchsample.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailRepository
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailViewModel(
    private val movieId: Int,
    private val movieDetailRepository: MovieDetailRepository
) : ViewModel() {

    val loadingState: LiveData<LoadingState>
        get() = _loadingState
    val movieDetail: LiveData<MovieDetailUiModel?>
        get() = _movieDetail

    private val _movieDetail = MutableLiveData<MovieDetailUiModel?>()
    private val _loadingState = MutableLiveData<LoadingState>()

    private val compositeDisposable = CompositeDisposable()

    fun updateMovieDetails() {
        _loadingState.postValue(LoadingState.LOADING)
        val disposable = movieDetailRepository.getMovieDetailObservable(movieId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { movieDetailUiModel: MovieDetailUiModel ->
                _movieDetail.postValue(movieDetailUiModel)
                _loadingState.postValue(LoadingState.SUCCESS)
            }
            .doOnError { _loadingState.postValue(LoadingState.ERROR) }
            .subscribe()

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    class Factory @Inject constructor(
        private val movieId: Int,
        private val movieDetailRepository: MovieDetailRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieDetailViewModel(
                movieId,
                movieDetailRepository
            ) as T
        }
    }
}