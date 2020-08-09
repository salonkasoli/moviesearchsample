package com.github.salonkasoli.moviesearchsample.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchRepository
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchUiState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchViewModel(
    private val repository: MovieSearchRepository
) : ViewModel() {

    val state: LiveData<MovieSearchUiState>
        get() = _state

    var currentQuery: String = ""

    private val _state = MutableLiveData<MovieSearchUiState>()

    private val compositeDisposable = CompositeDisposable()

    fun updateQuery(query: String) {
        currentQuery = query
        loadMoreMovies()
    }

    fun loadMoreMovies() {
        val query: String = currentQuery
        val oldState: MovieSearchCache = repository.getCached(query)

        if (oldState.isFullyLoaded() || query.isBlank()) {
            updateState(query, MovieSearchUiState(oldState.movies, LoadingState.SUCCESS))
            return
        }

        updateState(query, MovieSearchUiState(oldState.movies, LoadingState.LOADING))

        val disposable = repository.loadMoreObservable(query)
            .subscribeOn(Schedulers.io())
            .map { movieSearchCache: MovieSearchCache ->
                val newLoadingState = if (movieSearchCache.isFullyLoaded()) {
                    LoadingState.SUCCESS
                } else {
                    LoadingState.WAITING
                }
                return@map MovieSearchUiState(movieSearchCache.movies, newLoadingState)
            }
            .subscribe(
                { movieSearchUiState ->
                    updateState(query, movieSearchUiState)
                },
                {
                    updateState(query, MovieSearchUiState(oldState.movies, LoadingState.ERROR))
                }
            )

        compositeDisposable.add(disposable)
    }

    private fun updateState(query: String, newState: MovieSearchUiState) {
        if (query == currentQuery) {
            _state.postValue(newState)
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    class Factory @Inject constructor(
        private val movieSearchRepository: MovieSearchRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(movieSearchRepository) as T
        }
    }
}