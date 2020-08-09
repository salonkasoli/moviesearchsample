package com.github.salonkasoli.moviesearchsample.search

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistry
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

    fun handleState(savedStateRegistry: SavedStateRegistry) {
        savedStateRegistry.consumeRestoredStateForKey(BUNDLE_KEY)?.let {
            currentQuery = it.getString(SAVE_QUERY, "")
            _state.value = it.getSerializable(SAVE_STATE) as? MovieSearchUiState
        }
        savedStateRegistry.registerSavedStateProvider(BUNDLE_KEY, {
            Bundle().apply {
                putString(SAVE_QUERY, currentQuery)
                putSerializable(SAVE_STATE, _state.value)
            }
        })
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

    companion object {

        private const val BUNDLE_KEY = "search_bundle"
        private const val SAVE_QUERY = "query"
        private const val SAVE_STATE = "state"
    }
}